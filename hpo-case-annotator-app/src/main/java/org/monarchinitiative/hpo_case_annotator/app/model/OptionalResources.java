package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.springframework.stereotype.Component;

import java.io.File;

import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * This class is a POJO holding paths to HCA resources.
 * <p>
 * Created by Daniel Danis on 7/16/17.
 */
@Component
public class OptionalResources {

    private static final String SETUP_HINT = "Finish the setup in `File | Settings`.";

    private final ObjectProperty<File> diseaseCaseDir = new SimpleObjectProperty<>(this, "diseaseCaseDir");
    private final ObjectProperty<File> hpoPath = new SimpleObjectProperty<>(this, "ontologyPath");
    private final GenomicLocalResources genomicLocalResources = new GenomicLocalResources();
    private final FunctionalAnnotationResources functionalAnnotationResources = new FunctionalAnnotationResources();
    private final ListProperty<File> liftoverChainFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId");
    private final StringProperty softwareVersion = new SimpleStringProperty(this, "softwareVersion");

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

    public String getSoftwareVersion() {
        return softwareVersion.get();
    }

    public StringProperty softwareVersionProperty() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion.set(softwareVersion);
    }

    public StringBinding statusBinding() {
        return createStringBinding(() -> {
            String message = "All resources are set.";

            String biocuratorId = this.biocuratorId.get();
            if (biocuratorId == null || biocuratorId.isBlank())
                message = "Biocurator id is unset - saving/editing data is disabled. %s".formatted(SETUP_HINT);

            if (liftoverChainFiles.isEmpty())
                message = "Path(s) to Liftover chains are unset - liftover is disabled. %s".formatted(SETUP_HINT);

            if (genomicLocalResources.genomicResourcesAreUnset().get())
                message = "Genomic resources are unset - variant entry is disabled. %s".formatted(SETUP_HINT);

            if (functionalAnnotationResources.functionalResourcesAreUnset().get())
                message = "Path(s) to Jannovar file are unset - functional variant annotation is disabled. %s".formatted(SETUP_HINT);

            if (hpoPath.get() == null)
                message = "Path to HPO file is unset - phenotype annotation are disabled. %s".formatted(SETUP_HINT);

            return message;
        },
                hpoPath,
                genomicLocalResources.genomicResourcesAreUnset(),
                functionalAnnotationResources.functionalResourcesAreUnset(),
                liftoverChainFiles,
                biocuratorId);
    }
}
