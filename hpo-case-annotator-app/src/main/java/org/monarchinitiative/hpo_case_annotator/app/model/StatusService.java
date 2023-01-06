package org.monarchinitiative.hpo_case_annotator.app.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class StatusService {

    private final ListProperty<StatusMessage> messages = new SimpleListProperty<>(this, "messages", FXCollections.observableArrayList());

    public void addMessage(StatusMessage message) {
        messages.add(message);
    }

    public ObservableList<StatusMessage> getMessages() {
        return messages.get();
    }

    public ListProperty<StatusMessage> messagesProperty() {
        return messages;
    }
}
