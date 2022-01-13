package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;

import java.util.LinkedList;

public class ObservableStudyMetadata {
    private final StringProperty freeText = new SimpleStringProperty(this, "freeText");
    private final ObjectProperty<EditHistory> createdBy = new SimpleObjectProperty<>(this, "createdBy");
    private final ObservableList<EditHistory> modifiedBy = FXCollections.observableList(new LinkedList<>());

    public String getFreeText() {
        return freeText.get();
    }

    public void setFreeText(String freeText) {
        this.freeText.set(freeText);
    }

    public StringProperty freeTextProperty() {
        return freeText;
    }

    public EditHistory getCreatedBy() {
        return createdBy.get();
    }

    public void setCreatedBy(EditHistory createdBy) {
        this.createdBy.set(createdBy);
    }

    public ObjectProperty<EditHistory> createdByProperty() {
        return createdBy;
    }

    public ObservableList<EditHistory> modifiedBy() {
        return modifiedBy;
    }
}
