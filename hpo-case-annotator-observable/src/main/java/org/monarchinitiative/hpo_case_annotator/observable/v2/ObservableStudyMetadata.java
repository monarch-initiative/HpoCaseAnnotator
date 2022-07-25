package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ObservableStudyMetadata implements StudyMetadata, Updateable<StudyMetadata> {
    private final StringProperty freeText = new SimpleStringProperty(this, "freeText");
    private final ObjectProperty<ObservableEditHistory> createdBy = new SimpleObjectProperty<>(this, "createdBy", new ObservableEditHistory());
    private final ObservableList<ObservableEditHistory> modifiedBy = FXCollections.observableList(new LinkedList<>());

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
