package org.monarchinitiative.hpo_case_annotator.forms.v2.observable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Period;

public class ObservableAgeRange {

    private final ObjectProperty<Period> onset = new SimpleObjectProperty<>(this, "onset");
    private final ObjectProperty<Period> resolution = new SimpleObjectProperty<>(this, "resolution");

    public Period getOnset() {
        return onset.get();
    }

    public void setOnset(Period onset) {
        this.onset.set(onset);
    }

    public ObjectProperty<Period> onsetProperty() {
        return onset;
    }

    public Period getResolution() {
        return resolution.get();
    }

    public void setResolution(Period resolution) {
        this.resolution.set(resolution);
    }

    public ObjectProperty<Period> resolutionProperty() {
        return resolution;
    }

}
