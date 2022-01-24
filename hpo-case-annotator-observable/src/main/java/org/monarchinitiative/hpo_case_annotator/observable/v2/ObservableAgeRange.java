package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ObservableAgeRange {

    private final ObjectProperty<ObservableAge> onset = new SimpleObjectProperty<>(this, "onset", new ObservableAge());
    private final ObjectProperty<ObservableAge> resolution = new SimpleObjectProperty<>(this, "resolution", new ObservableAge());

    public ObservableAge getOnset() {
        return onset.get();
    }

    public void setOnset(ObservableAge onset) {
        this.onset.set(onset);
    }

    public ObjectProperty<ObservableAge> onsetProperty() {
        return onset;
    }

    public ObservableAge getResolution() {
        return resolution.get();
    }

    public void setResolution(ObservableAge resolution) {
        this.resolution.set(resolution);
    }

    public ObjectProperty<ObservableAge> resolutionProperty() {
        return resolution;
    }

    @Override
    public String toString() {
        return "ObservableAgeRange{" +
                "onset=" + onset.get() +
                ", resolution=" + resolution.get() +
                '}';
    }
}
