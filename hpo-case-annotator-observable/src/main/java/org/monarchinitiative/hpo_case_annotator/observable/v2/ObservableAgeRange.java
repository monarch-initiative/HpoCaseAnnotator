package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;

import java.util.stream.Stream;

public class ObservableAgeRange extends DeepObservable implements AgeRange {

    public static final Callback<ObservableAgeRange, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.start, obs.end};

    private final ObjectProperty<ObservableAge> start = new SimpleObjectProperty<>(this, "start");
    private final ObjectProperty<ObservableAge> end = new SimpleObjectProperty<>(this, "end");

    public ObservableAgeRange() {
    }

    public ObservableAgeRange(AgeRange ageRange) {
        if (ageRange != null) {
            if (ageRange.getStart() != null)
                start.set(new ObservableAge(ageRange.getStart()));

            if (ageRange.getEnd() != null)
                end.set(new ObservableAge(ageRange.getEnd()));
        }
    }

    @Override
    public ObservableAge getStart() {
        return start.get();
    }

    public void setStart(ObservableAge start) {
        this.start.set(start);
    }

    public ObjectProperty<ObservableAge> startProperty() {
        return start;
    }

    @Override
    public ObservableAge getEnd() {
        return end.get();
    }

    public void setEnd(ObservableAge end) {
        this.end.set(end);
    }

    public ObjectProperty<ObservableAge> endProperty() {
        return end;
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(start, end);
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservableAgeRange{" +
                "start=" + start.get() +
                ", end=" + end.get() +
                '}';
    }
}
