package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;

public class ObservableDiseaseStatus implements DiseaseStatus {

    private DiseaseIdentifier diseaseId;
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");

    public ObservableDiseaseStatus() {
    }

    public ObservableDiseaseStatus(DiseaseStatus diseaseStatus) {
        if (diseaseStatus != null) {
            if (diseaseStatus.getDiseaseId() != null)
                diseaseId = new DiseaseIdentifier(diseaseStatus.getDiseaseId());

            excluded.set(diseaseStatus.isExcluded());
        }
    }

    @Override
    public DiseaseIdentifier getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(DiseaseIdentifier diseaseId) {
        this.diseaseId = diseaseId;
    }

    @Override
    public boolean isExcluded() {
        return excluded.get();
    }

    public void setExcluded(boolean excluded) {
        this.excluded.set(excluded);
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }

    @Override
    public String toString() {
        return "ObservableDiseaseStatus{" +
                "diseaseId=" + diseaseId +
                ", excluded=" + excluded.get() +
                '}';
    }
}
