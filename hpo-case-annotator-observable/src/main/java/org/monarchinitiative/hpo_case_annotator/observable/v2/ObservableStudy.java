package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

public abstract class ObservableStudy implements Study {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication");
    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(this, "variants", FXCollections.observableArrayList());
    private final ObjectProperty<ObservableStudyMetadata> studyMetadata = new SimpleObjectProperty<>(this, "studyMetadata");

    public ObservableStudy() {
    }

    public ObservableStudy(Study study) {
        if (study != null) {
            id.set(study.getId());

            if (study.getPublication() != null)
                publication.set(new ObservablePublication(study.getPublication()));

            for (CuratedVariant variant : study.getVariants()) {
                if (variant != null)
                    variants.add(new ObservableCuratedVariant(variant));
            }

            if (study.getStudyMetadata() != null)
                studyMetadata.set(new ObservableStudyMetadata(study.getStudyMetadata()));
        }
    }

    @Override
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    @Override
    public ObservablePublication getPublication() {
        return publication.get();
    }

    public void setPublication(ObservablePublication publication) {
        this.publication.set(publication);
    }

    public ObjectProperty<ObservablePublication> publicationProperty() {
        return publication;
    }

    @Override
    public List<? extends ObservableCuratedVariant> getVariants() {
        return variants;
    }

    public void setVariants(ObservableList<ObservableCuratedVariant> variants) {
        this.variants.set(variants);
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    @Override
    public ObservableStudyMetadata getStudyMetadata() {
        return studyMetadata.get();
    }

    public void setStudyMetadata(ObservableStudyMetadata studyMetadata) {
        this.studyMetadata.set(studyMetadata);
    }

    public ObjectProperty<ObservableStudyMetadata> studyMetadataProperty() {
        return studyMetadata;
    }

    @Override
    public String toString() {
        return "ObservableStudy{" +
                "id=" + id.get() +
                ", publication=" + publication.get() +
                ", variants=" + variants.get() +
                ", studyMetadata=" + studyMetadata.get() +
                '}';
    }
}
