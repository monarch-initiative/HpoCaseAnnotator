package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

public class ObservableDiseaseStatus implements DiseaseStatus, Updateable<DiseaseStatus> {

    private final ObjectProperty<ObservableDiseaseIdentifier> diseaseId = new SimpleObjectProperty<>(this, "diseaseId", new ObservableDiseaseIdentifier());
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");

    @Override
    public ObservableDiseaseIdentifier getDiseaseId() {
        return diseaseId.get();
    }

    public void setDiseaseId(ObservableDiseaseIdentifier diseaseId) {
        this.diseaseId.set(diseaseId);
    }

    public ObjectProperty<ObservableDiseaseIdentifier> diseaseIdProperty() {
        return diseaseId;
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
    public void update(DiseaseStatus data) {
        if (data == null) {
            diseaseId.get().update(null);
            excluded.set(false);
        } else {
            diseaseId.get().update(data.getDiseaseId());
            excluded.set(data.isExcluded());
        }
    }
}
