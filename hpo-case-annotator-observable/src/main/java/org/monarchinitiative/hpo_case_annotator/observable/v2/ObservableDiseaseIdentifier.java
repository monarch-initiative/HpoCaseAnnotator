package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservableDiseaseIdentifier {

    private final ObjectProperty<TermId> diseaseId = new SimpleObjectProperty<>(this, "diseaseId");
    private final StringProperty diseaseName = new SimpleStringProperty(this, "diseaseName");

    public TermId getDiseaseId() {
        return diseaseId.get();
    }

    public ObjectProperty<TermId> diseaseIdProperty() {
        return diseaseId;
    }

    public void setDiseaseId(TermId diseaseId) {
        this.diseaseId.set(diseaseId);
    }

    public String getDiseaseName() {
        return diseaseName.get();
    }

    public StringProperty diseaseNameProperty() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }
}
