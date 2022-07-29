package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ObservableStudyMetadata implements StudyMetadata, Updateable<StudyMetadata> {
    private final StringProperty freeText = new SimpleStringProperty(this, "freeText");
    private final ObjectProperty<ObservableEditHistory> createdBy = new SimpleObjectProperty<>(this, "createdBy");
    private final ListProperty<ObservableEditHistory> modifiedBy = new SimpleListProperty<>(this, "modifiedBy", FXCollections.observableArrayList());

    public ObservableStudyMetadata() {
    }

    public ObservableStudyMetadata(StudyMetadata studyMetadata) {
        if (studyMetadata != null) {
            freeText.set(studyMetadata.getFreeText());
            if (studyMetadata.getCreatedBy() != null)
                createdBy.set(new ObservableEditHistory(studyMetadata.getCreatedBy()));

            for (EditHistory modified : studyMetadata.getModifiedBy()) {
                if (modified != null)
                    modifiedBy.add(new ObservableEditHistory(modified));
            }
        }
    }

    @Override
    public String getFreeText() {
        return freeText.get();
    }

    public void setFreeText(String freeText) {
        this.freeText.set(freeText);
    }

    public StringProperty freeTextProperty() {
        return freeText;
    }

    @Override
    public ObservableEditHistory getCreatedBy() {
        return createdBy.get();
    }

    public void setCreatedBy(ObservableEditHistory createdBy) {
        this.createdBy.set(createdBy);
    }

    public ObjectProperty<ObservableEditHistory> createdByProperty() {
        return createdBy;
    }

    @Override
    public ObservableList<ObservableEditHistory> getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(ObservableList<ObservableEditHistory> modifiedBy) {
        this.modifiedBy.set(modifiedBy);
    }

    public ListProperty<ObservableEditHistory> modifiedByProperty() {
        return modifiedBy;
    }

    @Override
    public void update(StudyMetadata data) {
        if (data == null) {
            freeText.set(null);
            createdBy.get().update(null);
            modifiedBy.clear();
        } else {
            freeText.set(data.getFreeText());
            createdBy.get().update(data.getCreatedBy());
            Updateable.updateObservableList(data.getModifiedBy(), modifiedBy, ObservableEditHistory::new);
        }

    }
}
