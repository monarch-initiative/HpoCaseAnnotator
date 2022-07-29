package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservableDiseaseIdentifier implements DiseaseIdentifier {

    private final ObjectProperty<TermId> diseaseId = new SimpleObjectProperty<>(this, "diseaseId");
    private final StringProperty diseaseName = new SimpleStringProperty(this, "diseaseName");

    public ObservableDiseaseIdentifier() {
    }

    public ObservableDiseaseIdentifier(DiseaseIdentifier diseaseIdentifier) {
        if (diseaseIdentifier != null) {
            diseaseId.set(diseaseIdentifier.id());
            diseaseName.set(diseaseIdentifier.getDiseaseName());
        }
    }

    @Override
    public TermId id() {
        return diseaseId.get();
    }

    public ObjectProperty<TermId> diseaseIdProperty() {
        return diseaseId;
    }

    public void setDiseaseId(TermId diseaseId) {
        this.diseaseId.set(diseaseId);
    }

    @Override
    public String getDiseaseName() {
        return diseaseName.get();
    }

    public StringProperty diseaseNameProperty() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }

    @Override
    public String toString() {
        return "ObservableDiseaseIdentifier{" +
                "diseaseId=" + diseaseId.get() +
                ", diseaseName=" + diseaseName.get() +
                '}';
    }
}
