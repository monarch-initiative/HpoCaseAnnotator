package org.monarchinitiative.hpo_case_annotator.forms.status;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;

import java.io.IOException;

public class StatusBar extends VBox {

    @FXML
    private TitledLabel message;

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

    }

    public StringProperty messageProperty() {
        return message.textProperty();
    }

    public void setMessage(String value) {
        message.setText(value);
    }

}
