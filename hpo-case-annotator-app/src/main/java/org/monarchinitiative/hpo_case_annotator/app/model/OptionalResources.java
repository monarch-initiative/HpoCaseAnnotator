package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * This class is a POJO holding paths to HCA resources.
 * <p>
 * Created by Daniel Danis on 7/16/17.
 */
@Component
public class OptionalResources {

    private final ObjectProperty<File> diseaseCaseDir = new SimpleObjectProperty<>(this, "diseaseCaseDir");
    private final ObjectProperty<File> hpoPath = new SimpleObjectProperty<>(this, "ontologyPath");
    private final GenomicLocalResources genomicLocalResources = new GenomicLocalResources();
    private final FunctionalAnnotationResources functionalAnnotationResources = new FunctionalAnnotationResources();
    private final ListProperty<File> liftoverChainFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId");

    public GenomicLocalResources getGenomicLocalResources() {
        return genomicLocalResources;
    }

    public FunctionalAnnotationResources getFunctionalAnnotationResources() {
        return functionalAnnotationResources;
    }

    public File getHpoPath() {
        return hpoPath.get();
    }

    public void setHpoPath(File hpoPath) {
        this.hpoPath.set(hpoPath);
    }

    public ObjectProperty<File> hpoPathProperty() {
        return hpoPath;
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

    public ListProperty<File> liftoverChainFilesProperty() {
        return liftoverChainFiles;
    }

}
