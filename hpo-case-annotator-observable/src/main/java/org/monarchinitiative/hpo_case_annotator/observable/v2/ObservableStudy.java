package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.LinkedList;

public class ObservableStudy {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication", new ObservablePublication());
    private final ObservableList<CuratedVariant> variants = FXCollections.observableList(new LinkedList<>());
    private final ObjectProperty<ObservableStudyMetadata> studyMetadata = new SimpleObjectProperty<>(this, "studyMetadata", new ObservableStudyMetadata());

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public ObservablePublication getPublication() {
        return publication.get();
    }

    public void setPublication(ObservablePublication publication) {
        this.publication.set(publication);
    }

    public ObjectProperty<ObservablePublication> publicationProperty() {
        return publication;
    }

    public ObservableList<CuratedVariant> variants() {
        return variants;
    }

    public ObservableStudyMetadata getStudyMetadata() {
        return studyMetadata.get();
    }

    public void setStudyMetadata(ObservableStudyMetadata studyMetadata) {
        this.studyMetadata.set(studyMetadata);
    }

    public ObjectProperty<ObservableStudyMetadata> studyMetadataProperty() {
        return studyMetadata;
    }
}
