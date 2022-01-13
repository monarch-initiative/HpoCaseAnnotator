package org.monarchinitiative.hpo_case_annotator.app;

import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.core.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.core.io.OMIMParser;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * The task reads the GUI resources listed below. Paths are read from {@link Properties} originating from
 * <code>hpo-case-annotator.properties</code>.
 * <p>
 * The following resources are parsed:
 * <ul>
 * <li>Path to reference genome directory</li>
 * <li>Human phenotype ontology OBO file</li>
 * <li>Entrez gene file</li>
 * <li>Curated files directory</li>
 * <li>Biocurator ID</li>
 * <li>OMIM tab file</li>
 * </ul>
 * <p>
 * Changes made by user are stored for the next run in {@link App#stop()} method.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
@Component
public class Startup implements ApplicationListener<ApplicationStartedEvent>, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

    private final OptionalResources optionalResources;

    private final Properties resourceProperties;

    public Startup(OptionalResources optionalResources, Properties resourceProperties) {
        this.optionalResources = optionalResources;
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
        try {
            optionalResources.setBiocuratorId(resourceProperties.getProperty(ResourcePaths.BIOCURATOR_ID_PROPERTY, ""));

            setupGenomicAssemblyRegistry(resourceProperties, optionalResources);

            setCuratedFilesFolder(resourceProperties.getProperty(ResourcePaths.DISEASE_CASE_DIR_PROPERTY), optionalResources);


            readOmim(optionalResources);

            String entrezPath = resourceProperties.getProperty(ResourcePaths.ENTREZ_GENE_PATH_PROPERTY);
            readEntrezGenes(entrezPath, optionalResources);

            String hpoPath = resourceProperties.getProperty(ResourcePaths.ONTOLOGY_PATH_PROPERTY);
            readHpo(hpoPath, optionalResources);
            LOGGER.info("Done");
        } catch (IOException | PhenolException e) {
            LOGGER.warn("Error during startup: {}", e.getMessage(), e);
        }
    }

    private static void setupGenomicAssemblyRegistry(Properties resourceProperties, OptionalResources optionalResources) {
        String hg18 = resourceProperties.getProperty(ResourcePaths.HG18_FASTA_PATH_PROPETY);
        if (hg18 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg18))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg18(resource));
        }

        String hg19 = resourceProperties.getProperty(ResourcePaths.HG19_FASTA_PATH_PROPETY);
        if (hg19 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg19))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg19(resource));
        }

        String hg38 = resourceProperties.getProperty(ResourcePaths.HG38_FASTA_PATH_PROPETY);
        if (hg38 != null) {
            GenomicLocalResource.createFromFastaPath(Paths.get(hg38))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg38(resource));
        }
    }

    private void setCuratedFilesFolder(String curatedDirPath, OptionalResources optionalResources) {
        if (curatedDirPath != null && new File(curatedDirPath).isDirectory()) {
            File curatedDir = new File(curatedDirPath);
            LOGGER.info("Setting curated files directory to '{}'", curatedDir.getAbsolutePath());
            optionalResources.setDiseaseCaseDir(curatedDir);
        } else
            LOGGER.info("Skipping setting of the curated files dictionary. Path '{}' does not point to directory", curatedDirPath);
    }

    private static void readOmim(OptionalResources optionalResources) throws IOException {
        String omimPath = "/data/omim.tsv";
        LOGGER.info("Parsing bundled OMIM file '{}'", omimPath);
        try (InputStream is = Startup.class.getResourceAsStream(omimPath)) {
            OMIMParser omimParser = new OMIMParser(is, StandardCharsets.UTF_8);
            optionalResources.setCanonicalName2mimid(omimParser.getCanonicalName2mimid());
            optionalResources.setMimid2canonicalName(omimParser.getMimid2canonicalName());
        }
    }

    private static void readEntrezGenes(String entrezPath, OptionalResources optionalResources) throws IOException {
        if (entrezPath != null && new File(entrezPath).isFile()) {
            File entrezGenesFile = new File(entrezPath);
            LOGGER.info("Loading Entrez genes from file '{}'", entrezGenesFile.getAbsolutePath());
            EntrezParser entrezParser = new EntrezParser(entrezGenesFile);
            entrezParser.readFile();
            optionalResources.setEntrezId2symbol(entrezParser.getEntrezId2symbol());
            optionalResources.setSymbol2entrezId(entrezParser.getSymbol2entrezId());
            optionalResources.setEntrezId2gene(entrezParser.getEntrezMap());
            optionalResources.setEntrezPath(entrezGenesFile);
        } else {
            LOGGER.info("Skipping loading Entrez genes since the location is unset");
        }
    }

    private static void readHpo(String ontologyPath, OptionalResources optionalResources) throws IOException, PhenolException {
        if (ontologyPath != null && new File(ontologyPath).isFile()) {
            File ontologyFile = new File(ontologyPath);
            LOGGER.info("Loading HPO from file '{}'", ontologyFile.getAbsolutePath());
            optionalResources.setOntologyPath(ontologyFile);
            optionalResources.setOntology(OntologyLoader.loadOntology(ontologyFile));
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
