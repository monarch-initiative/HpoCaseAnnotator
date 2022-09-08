package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class StatusBar extends HBox {

    private final ObservableList<String> messages = FXCollections.observableArrayList();

    @FXML
    private Label summaryLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button clearButton;

    public StatusBar() {
        FXMLLoader loader = new FXMLLoader(StatusBar.class.getResource("StatusBar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        StringBinding summaryBinding = Bindings.createStringBinding(
                () -> switch (messages.size()) {
                    case 0 -> "OK";
                    case 1 -> "1 error";
                    default -> messages.size() + " errors";
                },
                messages);
        summaryLabel.textProperty().bind(summaryBinding);

        StringBinding firstMessageBinding = Bindings.createStringBinding(
                () -> (messages.isEmpty()) ? "" : messages.get(0),
                messages);
        messageLabel.textProperty().bind(firstMessageBinding);

        BooleanBinding noMessagesBinding = Bindings.createBooleanBinding(messages::isEmpty, messages);
        clearButton.disableProperty().bind(noMessagesBinding);
    }

    public void showMessage(String message) {
        showMessages(message);
    }
    public void showMessages(String... messages) {
        this.messages.addAll(messages);
    }

    @FXML
    private void messageLabelMouseClicked(MouseEvent mouseEvent) {

        mouseEvent.consume();
    }

    @FXML
    private void clearButtonAction() {
        this.messages.clear();
    }
}