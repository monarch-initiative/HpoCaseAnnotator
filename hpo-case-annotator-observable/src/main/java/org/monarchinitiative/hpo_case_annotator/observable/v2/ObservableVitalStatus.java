package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;

public class ObservableVitalStatus implements VitalStatus {

    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(this, "status");
    private final ObjectProperty<TimeElement> timeOfDeath = new SimpleObjectProperty<>(this, "timeOfDeath");

    public ObservableVitalStatus() {
    }

    public ObservableVitalStatus(VitalStatus vitalStatus) {
        if (vitalStatus != null) {
            status.set(vitalStatus.getStatus());
            timeOfDeath.set(vitalStatus.getTimeOfDeath());
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
    public TimeElement getTimeOfDeath() {
        return timeOfDeath.get();
    }

    public void setTimeOfDeath(TimeElement timeOfDeath) {
        this.timeOfDeath.set(timeOfDeath);
    }

    public ObjectProperty<TimeElement> timeOfDeathProperty() {
        return timeOfDeath;
    }

    @Override
    public String toString() {
        return "ObservableVitalStatus{" +
                "status=" + status.get() +
                ", timeOfDeath=" + timeOfDeath.get() +
                '}';
    }
}
