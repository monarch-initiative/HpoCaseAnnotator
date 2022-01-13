package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.property.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservableDiseaseStatus {

    private final ObjectProperty<TermId> diseaseId = new SimpleObjectProperty<>(this, "diseaseId");
    private final StringProperty diseaseName = new SimpleStringProperty(this, "diseaseName");
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");

    public TermId getDiseaseId() {
        return diseaseId.get();
    }

    public void setDiseaseId(TermId diseaseId) {
        this.diseaseId.set(diseaseId);
    }

    public ObjectProperty<TermId> diseaseIdProperty() {
        return diseaseId;
    }

    public String getDiseaseName() {
        return diseaseName.get();
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName.set(diseaseName);
    }

    public StringProperty diseaseNameProperty() {
        return diseaseName;
    }

    public boolean isExcluded() {
        return excluded.get();
    }

    public void setExcluded(boolean excluded) {
        this.excluded.set(excluded);
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }
}
