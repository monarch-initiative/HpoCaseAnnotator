package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;

public abstract class ObservableStudy implements Study {

    private final StringProperty id = new SimpleStringProperty(this, "id");
    private final ObjectProperty<ObservablePublication> publication = new SimpleObjectProperty<>(this, "publication", new ObservablePublication());
    private final ObservableList<CuratedVariant> variants = FXCollections.observableList(new LinkedList<>());
    private final ObjectProperty<ObservableStudyMetadata> studyMetadata = new SimpleObjectProperty<>(this, "studyMetadata", new ObservableStudyMetadata());

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

    public ObservableList<CuratedVariant> variants() {
        return variants;
    }

    @Override
    public List<? extends CuratedVariant> getVariants() {
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



    public <U extends Study> void updateStudy(U data) {
        if (data == null) {
            setId(null);
            publication.get().update(null);
            variants.clear();
            studyMetadata.get().update(null);
        } else {
            setId(data.getId());
            publication.get().update(data.getPublication());
            // TODO - finish when we have observable variants.
//            Updateable.updateObservableList(data.getVariants(), variants, Obser);
            studyMetadata.get().update(data.getStudyMetadata());
        }
    }

    @Override
    public String toString() {
        return "ObservableStudy{" +
                "id=" + id +
                ", publication=" + publication +
                ", variants=" + variants +
                ", studyMetadata=" + studyMetadata +
                '}';
    }
}
