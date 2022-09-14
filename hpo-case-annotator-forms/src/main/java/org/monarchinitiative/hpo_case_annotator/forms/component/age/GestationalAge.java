package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;

import java.io.IOException;

public class GestationalAge extends VBox {

    @FXML
    private TitledLabel gestationalWeeks;
    @FXML
    private TitledLabel gestationalDays;

    public GestationalAge() {
        FXMLLoader loader = new FXMLLoader(GestationalAge.class.getResource("GestationalAge.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringProperty weeksProperty() {
        return gestationalWeeks.textProperty();
    }

    public StringProperty daysProperty() {
        return gestationalDays.textProperty();
    }
}
