package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Utils {

    private Utils() {}

    public static void closeTheStage(ActionEvent e) {
        Button okButton = (Button) e.getSource();
        Stage stage = (Stage) okButton.getParent().getScene().getWindow();
        stage.close();
    }
}
