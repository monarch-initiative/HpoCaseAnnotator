package org.monarchinitiative.hpo_case_annotator.app.controller.util;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.app.model.StatusMessage;
import org.monarchinitiative.hpo_case_annotator.app.model.StatusService;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class StatusBar extends HBox {

    private final ObjectProperty<StatusService> statusServiceProperty = new SimpleObjectProperty<>();

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

    public ObjectProperty<StatusService> statusServiceProperty() {
        return statusServiceProperty;
    }

    @FXML
    private void initialize() {
        statusServiceProperty.addListener((obs, old, novel) -> {
            if (novel != null) {
                novel.messagesProperty().sizeProperty()
                        .addListener((sizeObservable, oldSize, newSize) ->
                                summaryLabel.setText(switch (newSize.intValue()) {
                                    case 0 -> "OK";
                                    case 1 -> "1 issue";
                                    default -> "%d issues".formatted(newSize.intValue());
                                }));
            }
        });


        ObjectBinding<ListProperty<StatusMessage>> messages = select(statusServiceProperty, "messages");

        ObjectBinding<StatusMessage> messageBinding = when(messages.isNull())
                .then((StatusMessage) null)
                .otherwise(valueAt(messages.get(), 0));

        messageLabel.textProperty().bind(when(messageBinding.isNotNull())
                .then(messageBinding.getValue().messageProperty())
                .otherwise((String) null));


        clearButton.disableProperty().bind(messages.isNull().or(messages.get().emptyProperty()));
    }

    @FXML
    private void messageLabelMouseClicked(MouseEvent mouseEvent) {

        mouseEvent.consume();
    }

    @FXML
    private void clearButtonAction() {
        StatusService service = statusServiceProperty.get();
        if (service != null)
            service.messagesProperty().clear();
    }
}
