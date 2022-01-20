package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class IndividualDetailController {
    @FXML
    private Label idLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label ageLabel;

    public StringProperty idProperty() {
        return idLabel.textProperty();
    }

    public StringProperty sexProperty() {
        return sexLabel.textProperty();
    }

    public StringProperty ageLabel() {
        return ageLabel.textProperty();
    }

}
