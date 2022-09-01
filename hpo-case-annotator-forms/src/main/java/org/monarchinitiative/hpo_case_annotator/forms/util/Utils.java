package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import static javafx.beans.binding.Bindings.*;

public class Utils {

    private Utils() {}

    public static void closeTheStage(ActionEvent e) {
        Button okButton = (Button) e.getSource();
        Stage stage = (Stage) okButton.getParent().getScene().getWindow();
        stage.close();
    }

    public static StringBinding nullableStringProperty(ObjectProperty<?> property, String propertyId) {
        // 2 select statements but can't be done better.
        return when(select(property, propertyId).isNotNull())
                .then(selectString(property, propertyId))
                .otherwise("N/A");
    }
}
