package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.app.controller.Loaders;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverAdapter;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverService;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.model.proto.Gene;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class is a POJO holding paths to HCA resources.
 * <p>
 * Created by Daniel Danis on 7/16/17.
 */
@Component
public class OptionalResources {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalResources.class);

    private final BooleanBinding omimIsMissing;

    private final BooleanBinding diseaseCaseDirIsInitialized;

    private final ObjectProperty<File> diseaseCaseDir = new SimpleObjectProperty<>(this, "diseaseCaseDir");
    private final ObjectProperty<File> ontologyPath = new SimpleObjectProperty<>(this, "ontologyPath");
    private final ObjectProperty<GenomicLocalResources> genomicLocalResources = new SimpleObjectProperty<>(this, "genomicResources", new GenomicLocalResources());
    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>(this, "genomicAssemblyRegistry", new GenomicAssemblyRegistry());
    private final ObjectProperty<FunctionalAnnotationResources> functionalAnnotationResources = new SimpleObjectProperty<>(this, "functionalAnnotationResources", new FunctionalAnnotationResources());
    private final ObservableList<File> liftoverChainFiles = FXCollections.observableList(new LinkedList<>());
    private final ObjectProperty<LiftOverService> liftoverService = new SimpleObjectProperty<>(this, "liftoverService");

    // default value does not harm here
    private final StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId", "");

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    private final ObservableList<DiseaseIdentifier> diseaseIdentifiers = FXCollections.observableList(new LinkedList<>());

    @Deprecated
    private final ObjectProperty<Map<String, String>> mimid2canonicalName = new SimpleObjectProperty<>(this, "mimid2canonicalName");

    @Deprecated
    private final ObjectProperty<Map<String, String>> canonicalName2mimid = new SimpleObjectProperty<>(this,"canonicalName2mimid");

    public OptionalResources() {
        this.omimIsMissing = Bindings.createBooleanBinding(() -> Stream.of(mimid2canonicalNameProperty(),
                canonicalName2mimidProperty()).anyMatch(op -> op.get() == null),
                mimid2canonicalNameProperty(), canonicalName2mimidProperty());

        this.diseaseCaseDirIsInitialized = Bindings.createBooleanBinding(() -> getDiseaseCaseDir() != null && getDiseaseCaseDir().isDirectory(),
                diseaseCaseDirProperty());
        ontologyPath.addListener(loadOntologyWhenFileIsValid());
        liftoverChainFiles.addListener(initializeLiftoverServiceWhenFolderIsValid());
    }

    private ListChangeListener<? super File> initializeLiftoverServiceWhenFolderIsValid() {
        return change -> {
            while (change.next()) {
                liftoverService.set(LiftOverAdapter.ofChains(change.getList().toArray(File[]::new)));
            }
        };
    }

    private ChangeListener<File> loadOntologyWhenFileIsValid() {
        return (obs, old, novel) -> {
            if (novel != null) {
                if (!novel.isFile()) {
                    LOGGER.warn("Path to HPO does not point to a file: `{}`", novel.getAbsolutePath());
                    return;
                }
                setOntology(Loaders.loadOntology(novel.toPath()));
            }
        };
    }


    /**
     * @return <code>true</code> if the diseaseCaseDir is not <code>null</code> and is a directory
     */
    public Boolean getDiseaseCaseDirIsInitialized() {
        return diseaseCaseDirIsInitialized.get();
    }

    public GenomicLocalResources getGenomicLocalResources() {
        return genomicLocalResources.get();
    }

    public ObjectProperty<GenomicLocalResources> genomicLocalResourcesProperty() {
        return genomicLocalResources;
    }

    public void setGenomicLocalResources(GenomicLocalResources genomicLocalResources) {
        this.genomicLocalResources.set(genomicLocalResources);
    }

    public GenomicAssemblyRegistry getGenomicAssemblyRegistry() {
        return genomicAssemblyRegistry.get();
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }

    public void setGenomicAssemblyRegistry(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        this.genomicAssemblyRegistry.set(genomicAssemblyRegistry);
    }

    public FunctionalAnnotationResources getFunctionalAnnotationResources() {
        return functionalAnnotationResources.get();
    }

    public ObjectProperty<FunctionalAnnotationResources> functionalAnnotationResourcesProperty() {
        return functionalAnnotationResources;
    }

    public File getOntologyPath() {
        return ontologyPath.get();
    }


    public void setOntologyPath(File ontologyPath) {
        this.ontologyPath.set(ontologyPath);
    }


    public ObjectProperty<File> ontologyPathProperty() {
        return ontologyPath;
    }

    public Boolean getOmimIsMissing() {
        return omimIsMissing.get();
    }


    public BooleanBinding omimIsMissingProperty() {
        return omimIsMissing;
    }


    public Map<String, String> getMimid2canonicalName() {
        return mimid2canonicalName.get();
    }


    public void setMimid2canonicalName(Map<String, String> mimid2canonicalName) {
        this.mimid2canonicalName.set(mimid2canonicalName);
    }


    public ObjectProperty<Map<String, String>> mimid2canonicalNameProperty() {
        return mimid2canonicalName;
    }


    public Map<String, String> getCanonicalName2mimid() {
        return canonicalName2mimid.get();
    }


    public void setCanonicalName2mimid(Map<String, String> canonicalName2mimid) {
        this.canonicalName2mimid.set(canonicalName2mimid);
    }


    public ObjectProperty<Map<String, String>> canonicalName2mimidProperty() {
        return canonicalName2mimid;
    }

    public ObservableList<DiseaseIdentifier> diseaseIdentifiers() {
        return diseaseIdentifiers;
    }

    public File getDiseaseCaseDir() {
        return diseaseCaseDir.get();
    }


    public void setDiseaseCaseDir(File diseaseCaseDir) {
        this.diseaseCaseDir.set(diseaseCaseDir);
    }


    public ObjectProperty<File> diseaseCaseDirProperty() {
        return diseaseCaseDir;
    }


    public String getBiocuratorId() {
        return biocuratorId.get();
    }


    public void setBiocuratorId(String biocuratorId) {
        this.biocuratorId.set(biocuratorId);
    }


    public StringProperty biocuratorIdProperty() {
        return biocuratorId;
    }


    public Ontology getOntology() {
        return ontology.get();
    }


    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }


    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public ObservableList<File> liftoverChainFiles() {
        return liftoverChainFiles;
    }

    public ObjectProperty<LiftOverService> liftoverServiceProperty() {
        return liftoverService;
    }
}
