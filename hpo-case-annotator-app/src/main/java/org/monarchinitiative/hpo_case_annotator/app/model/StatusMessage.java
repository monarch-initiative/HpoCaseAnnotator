package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class StatusMessage {
    private final ObjectProperty<StatusLevel> level = new SimpleObjectProperty<>(this, "level");
    private final StringProperty message = new SimpleStringProperty(this, "message");

    public StatusLevel getLevel() {
        return level.get();
    }

    public ObjectProperty<StatusLevel> levelProperty() {
        return level;
    }

    public void setLevel(StatusLevel level) {
        this.level.set(level);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StatusMessage) obj;
        return Objects.equals(this.level, that.level) &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, message);
    }

    @Override
    public String toString() {
        return "StatusMessage[" +
                "level=" + level.get() + ", " +
                "message=" + message.get() + ']';
    }

}
