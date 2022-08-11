package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;

import java.time.Instant;

public class ObservableEditHistory implements EditHistory {

    public static ObservableEditHistory defaultInstance() {
        return new ObservableEditHistory();
    }

    private final StringProperty curatorId = new SimpleStringProperty(this, "curatorId");
    private final StringProperty softwareVersion = new SimpleStringProperty(this, "softwareVersion");
    private final ObjectProperty<Instant> timestamp = new SimpleObjectProperty<>(this, "timestamp");

    public ObservableEditHistory() {
    }

    public ObservableEditHistory(EditHistory editHistory) {
        if (editHistory != null) {
            curatorId.set(editHistory.getCuratorId());
            softwareVersion.set(editHistory.getSoftwareVersion());

            if (editHistory.getTimestamp() != null)
                timestamp.set(editHistory.getTimestamp());
        }
    }

    @Override
    public String getCuratorId() {
        return curatorId.get();
    }

    public void setCuratorId(String curatorId) {
        this.curatorId.set(curatorId);
    }

    public StringProperty curatorIdProperty() {
        return curatorId;
    }

    @Override
    public String getSoftwareVersion() {
        return softwareVersion.get();
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion.set(softwareVersion);
    }

    public StringProperty softwareVersionProperty() {
        return softwareVersion;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp.get();
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp.set(timestamp);
    }

    public ObjectProperty<Instant> timestampProperty() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ObservableEditHistory{" +
                "curatorId=" + curatorId.get() +
                ", softwareVersion=" + softwareVersion.get() +
                ", timestamp=" + timestamp.get() +
                '}';
    }
}
