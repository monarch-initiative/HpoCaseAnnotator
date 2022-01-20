package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;

public class ObservableDiseaseStatus {

    private final ObjectProperty<ObservableDiseaseIdentifier> diseaseIdentifier = new SimpleObjectProperty<>(this, "diseaseIdentifier", new ObservableDiseaseIdentifier());
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");

    public ObservableDiseaseIdentifier getDiseaseIdentifier() {
        return diseaseIdentifier.get();
    }

    public void setDiseaseIdentifier(ObservableDiseaseIdentifier diseaseIdentifier) {
        this.diseaseIdentifier.set(diseaseIdentifier);
    }

    public ObjectProperty<ObservableDiseaseIdentifier> diseaseIdentifierProperty() {
        return diseaseIdentifier;
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
