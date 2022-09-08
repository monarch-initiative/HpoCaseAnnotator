package org.monarchinitiative.hpo_case_annotator.app;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.app.model.FunctionalAnnotationResources;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalServices;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResources;
import org.monarchinitiative.hpo_case_annotator.app.task.*;
import org.monarchinitiative.hpo_case_annotator.app.util.GenomicLocalResourceValidator;
import org.monarchinitiative.hpo_case_annotator.core.data.DiseaseIdentifierService;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.core.reference.functional.FunctionalAnnotationService;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.GenomicAssemblyService;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * The task reads locations of the GUI resources from {@link Properties} originating from
 * <code>hpo-case-annotator.properties</code>.
 * <p>
 * The following resources are parsed:
 * <ul>
 * <li>Path to reference genome directory</li>
 * <li>Human phenotype ontology OBO file</li>
 * <li>Curated files directory</li>
 * <li>Biocurator ID</li>
 * <li>OMIM tab file</li>
 * <li>Reference genome files</li>
 * <li>Jannovar transcript databases</li>
 * </ul>
 * <p>
 * Changes made by user are stored for the next run in {@link App#stop()} method.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
@Component
public class Startup implements ApplicationListener<ApplicationStartedEvent>, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);
    // Location of the bundled OMIM file.
    private static final String DATA_OMIM_TSV = "/data/omim.tsv";

    private final ExecutorService executorService;
    private final OptionalResources optionalResources;
    private final OptionalServices optionalServices;

    private final GenomicRemoteResources genomicRemoteResources;

    private final Properties resourceProperties;

    public Startup(ExecutorService executorService,
                   OptionalResources optionalResources,
                   OptionalServices optionalServices,
                   GenomicRemoteResources genomicRemoteResources,
                   Properties resourceProperties) {
        this.executorService = executorService;
        this.optionalResources = optionalResources;
        this.optionalServices = optionalServices;
        this.genomicRemoteResources = genomicRemoteResources;
        this.resourceProperties = resourceProperties;
    }

    /**
     * Read {@link Properties} and initialize app resources in the {@link OptionalResources}:
     *
     * <ul>
     * <li>HPO ontology</li>
     * <li>Entrez gene file</li>
     * <li>Curated files directory</li>
     * <li>Biocurator ID, and </li>
     * <li>OMIM file</li>
     * </ul>
     */
    @Override
    public void run() {
        LOGGER.debug("Triggering startup tasks");
        optionalServices.setExecutorService(executorService);
        triggerLoadingTasks(executorService, optionalResources, optionalServices);

        LOGGER.debug("Setting resource locations");
        optionalResources.setBiocuratorId(resourceProperties.getProperty(ResourcePaths.BIOCURATOR_ID_PROPERTY, null));

        setGenomicAssemblyResourceLocations();
        setFunctionalAnnotationResourceLocations();
        setCuratedFilesFolder();
        setLiftoverChainLocation();
        setHpoLocation();

        LOGGER.info("Startup complete");
    }

    private static void triggerLoadingTasks(ExecutorService executor,
                                            OptionalResources resources,
                                            OptionalServices services) {
        // Load HPO
        resources.hpoPathProperty()
                .addListener(loadHpo(executor, services));

        // Load Liftover
        resources.liftoverChainFilesProperty()
                .addListener(loadLiftover(executor, services));

        // Load Jannovar
        resources.getFunctionalAnnotationResources()
                .addListener(loadFunctionalResources(executor, services, resources.getFunctionalAnnotationResources()));

        // Genomic data
        resources.getGenomicLocalResources()
                .addListener(loadGenomicResources(executor, services, resources.getGenomicLocalResources()));

        // Diseases are loaded from the bundled file right now
        loadOmimDiseases(executor, services, DATA_OMIM_TSV);
    }

    private static ChangeListener<File> loadHpo(ExecutorService executor,
                                                OptionalServices services) {
        return (obs, old, novel) -> {
            if (novel == null)
                services.setHpo(null);
            else {
                Task<Ontology> task = new LoadOntology(novel.toPath());
                task.setOnSucceeded(e -> services.setHpo(task.getValue()));
                task.setOnFailed(e -> {
                    // TODO - add meaningful error reporting
                    LOGGER.error("Ontology load failed");
                });
                executor.submit(task);
            }
        };
    }

    private static ListChangeListener<? super File> loadLiftover(ExecutorService executor,
                                                                 OptionalServices services) {
        return change -> {
            //noinspection StatementWithEmptyBody
            while (change.next()) {
                // Just get to the last change.
            }
            Task<LiftOverService> task = new LoadLiftOverService(change.getList());

            task.setOnSucceeded(e -> services.setLiftoverService(task.getValue()));
            task.setOnFailed(e -> {
                // TODO - add meaningful error reporting
                LOGGER.error("Liftover chain load failed");
            });
            executor.submit(task);
        };
    }

    private static InvalidationListener loadFunctionalResources(ExecutorService executor,
                                                                OptionalServices services,
                                                                FunctionalAnnotationResources far) {
        return obs -> {
            Path path;
            Consumer<FunctionalAnnotationService> consumer;
            if (obs.equals(far.hg19JannovarPathProperty())) {
                path = far.getHg19JannovarPath();
                consumer = service -> services.getFunctionalAnnotationRegistry().setHg19Service(service);
            } else if (obs.equals(far.hg38JannovarPathProperty())) {
                path = far.getHg38JannovarPath();
                consumer = service -> services.getFunctionalAnnotationRegistry().setHg38Service(service);
            } else {
                LOGGER.warn("Unexpected observable in `loadFunctionalResources()`");
                return;
            }

            Task<FunctionalAnnotationService> task = new LoadJannovarFunctionalAnnotationService(path);
            task.setOnSucceeded(e -> consumer.accept(task.getValue()));
            task.setOnFailed(e -> {
                // TODO - add meaningful error reporting
                LOGGER.error("Loading of functional annotation service failed");
            });

            executor.submit(task);
        };
    }

    private static InvalidationListener loadGenomicResources(ExecutorService executor,
                                                             OptionalServices services,
                                                             GenomicLocalResources genomicResources) {
        return obs -> {
            GenomicLocalResource resource;
            Consumer<GenomicAssemblyService> consumer;
            if (obs.equals(genomicResources.hg18Property())) {
                resource = genomicResources.getHg18();
                consumer = service -> services.getGenomicAssemblyRegistry().setHg18Service(service);
            } else if (obs.equals(genomicResources.hg19Property())) {
                resource = genomicResources.getHg19();
                consumer = service -> services.getGenomicAssemblyRegistry().setHg19Service(service);
            } else if (obs.equals(genomicResources.hg38Property())) {
                resource = genomicResources.getHg38();
                consumer = service -> services.getGenomicAssemblyRegistry().setHg38Service(service);
            } else {
                LOGGER.warn("Unexpected observable in `loadGenomicResources()`");
                return;
            }

            Task<GenomicAssemblyService> task = new LoadGenomicAssemblyService(resource);
            task.setOnSucceeded(e -> consumer.accept(task.getValue()));
            task.setOnFailed(e -> {
                // TODO - add meaningful error reporting
                LOGGER.error("Loading of genomic resource failed");
            });

            executor.submit(task);
        };
    }

    private static void loadOmimDiseases(ExecutorService executor,
                                         OptionalServices services,
                                         String omimPath) {
        Task<DiseaseIdentifierService> task = new LoadOmimDiseases(omimPath);
        task.setOnSucceeded(e -> services.setDiseaseIdentifierService(task.getValue()));
        task.setOnFailed(e -> {
            // TODO - add meaningful error reporting
            LOGGER.error("Loading of the bundled OMIM disease table failed");
        });
        executor.submit(task);
    }

    private void setFunctionalAnnotationResourceLocations() {
        String hg19 = resourceProperties.getProperty(ResourcePaths.HG19_JANNOVAR_CACHE_PATH);
        if (hg19 != null)
            optionalResources.getFunctionalAnnotationResources().setHg19JannovarPath(Paths.get(hg19));

        String hg38 = resourceProperties.getProperty(ResourcePaths.HG38_JANNOVAR_CACHE_PATH);
        if (hg38 != null)
            optionalResources.getFunctionalAnnotationResources().setHg38JannovarPath(Paths.get(hg38));
    }

    private void setGenomicAssemblyResourceLocations() {
        GenomicLocalResourceValidator validator = GenomicLocalResourceValidator.of(LOGGER::debug);
        String hg18 = resourceProperties.getProperty(ResourcePaths.HG18_FASTA_PATH_PROPETY);
        if (hg18 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg18))
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg18()))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg18(resource));
        }

        String hg19 = resourceProperties.getProperty(ResourcePaths.HG19_FASTA_PATH_PROPETY);
        if (hg19 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg19))
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg19()))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg19(resource));
        }

        String hg38 = resourceProperties.getProperty(ResourcePaths.HG38_FASTA_PATH_PROPETY);
        if (hg38 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg38))
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg38()))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg38(resource));
        }
    }

    private void setCuratedFilesFolder() {
        String curatedDirPath = resourceProperties.getProperty(ResourcePaths.DISEASE_CASE_DIR_PROPERTY);
        if (curatedDirPath != null && new File(curatedDirPath).isDirectory()) {
            File curatedDir = new File(curatedDirPath);
            LOGGER.debug("Setting curated files directory to '{}'", curatedDir.getAbsolutePath());
            optionalResources.setDiseaseCaseDir(curatedDir);
        } else
            LOGGER.info("Skipping setting of the curated files dictionary. Path '{}' does not point to directory", curatedDirPath);
    }

    private void setLiftoverChainLocation() {
        String liftoverChains = resourceProperties.getProperty(ResourcePaths.LIFTOVER_CHAIN_PATHS_PROPERTY);
        if (liftoverChains != null) {
            List<File> chainFiles = Arrays.stream(liftoverChains.split(ResourcePaths.LIFTOVER_CHAIN_PATH_SEPARATOR))
                    .map(File::new)
                    .toList();
            optionalResources.liftoverChainFilesProperty().setAll(chainFiles);
        }
    }

    private void setHpoLocation() {
        String hpoPath = resourceProperties.getProperty(ResourcePaths.ONTOLOGY_PATH_PROPERTY);
        if (hpoPath != null && new File(hpoPath).isFile()) {
            File ontologyFile = new File(hpoPath);
            optionalResources.setHpoPath(ontologyFile);
        } else {
            LOGGER.info("Skipping loading HPO file since the location is unset");
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ExecutorService executorService = event.getApplicationContext().getBean(ExecutorService.class);
        executorService.submit(this);
    }

}
