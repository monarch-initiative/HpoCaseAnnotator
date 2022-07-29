package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

public class ObservableAgeRange implements AgeRange, Updateable<AgeRange> {

    private final ObjectProperty<ObservableAge> onset = new SimpleObjectProperty<>(this, "onset", new ObservableAge());
    private final ObjectProperty<ObservableAge> resolution = new SimpleObjectProperty<>(this, "resolution", new ObservableAge());

    @Override
    public ObservableAge getOnset() {
        return onset.get();
    }

    public void setOnset(ObservableAge onset) {
        this.onset.set(onset);
    }

    @Override
    public ObservableAge getResolution() {
        return resolution.get();
    }

    public void setResolution(ObservableAge resolution) {
        this.resolution.set(resolution);
    }

    public ObjectProperty<ObservableAge> onsetProperty() {
        return onset;
    }

    public ObjectProperty<ObservableAge> resolutionProperty() {
        return resolution;
    }

    @Override
    public void update(AgeRange data) {
        if (data == null) {
            onset.get().update(null);
            resolution.get().update(null);
        } else {
            onset.get().update(data.getOnset());
            resolution.get().update(data.getResolution());
        }
    }

    @Override
    public String toString() {
        return "ObservableAgeRange{" +
                "onset=" + onset.get() +
                ", resolution=" + resolution.get() +
                '}';
    }
}
