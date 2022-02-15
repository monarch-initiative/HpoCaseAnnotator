package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.nio.file.Path;

public class FunctionalAnnotationResources {

    private final ObjectProperty<Path> hg19JannovarPath = new SimpleObjectProperty<>(this, "hg19JannovarPath");
    private final ObjectProperty<Path> hg38JannovarPath = new SimpleObjectProperty<>(this, "hg38JannovarPath");

    public Path getHg19JannovarPath() {
        return hg19JannovarPath.get();
    }

    public void setHg19JannovarPath(Path hg19JannovarPath) {
        this.hg19JannovarPath.set(hg19JannovarPath);
    }

    public ObjectProperty<Path> hg19JannovarPathProperty() {
        return hg19JannovarPath;
    }

    public Path getHg38JannovarPath() {
        return hg38JannovarPath.get();
    }

    public void setHg38JannovarPath(Path hg38JannovarPath) {
        this.hg38JannovarPath.set(hg38JannovarPath);
    }

    public ObjectProperty<Path> hg38JannovarPathProperty() {
        return hg38JannovarPath;
    }
}
