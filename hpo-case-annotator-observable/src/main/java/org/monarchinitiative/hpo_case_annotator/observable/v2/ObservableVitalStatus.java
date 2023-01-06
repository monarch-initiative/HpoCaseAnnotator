package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.deep.DeepObservable;

import java.util.stream.Stream;

public class ObservableVitalStatus extends DeepObservable implements VitalStatus {

    public static final Callback<ObservableVitalStatus, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.status, obs.timeOfDeath};

    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(this, "status");
    private final ObjectProperty<ObservableTimeElement> timeOfDeath = new SimpleObjectProperty<>(this, "timeOfDeath");

    public ObservableVitalStatus() {
    }

    public ObservableVitalStatus(VitalStatus vitalStatus) {
        if (vitalStatus != null) {
            status.set(vitalStatus.getStatus());

            if (vitalStatus.getTimeOfDeath() != null)
                timeOfDeath.set(new ObservableTimeElement(vitalStatus.getTimeOfDeath()));
        }
    }

    @Override
    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    @Override
    public ObservableTimeElement getTimeOfDeath() {
        return timeOfDeath.get();
    }

    public void setTimeOfDeath(ObservableTimeElement timeOfDeath) {
        this.timeOfDeath.set(timeOfDeath);
    }

    public ObjectProperty<ObservableTimeElement> timeOfDeathProperty() {
        return timeOfDeath;
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(timeOfDeath);
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservableVitalStatus{" +
                "status=" + status.get() +
                ", timeOfDeath=" + timeOfDeath.get() +
                '}';
    }
}
