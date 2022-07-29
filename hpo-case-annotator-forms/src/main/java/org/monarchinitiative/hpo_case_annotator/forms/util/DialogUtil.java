package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class DialogUtil {

    public static final ButtonType[] UPDATE_CANCEL_BUTTONS = new ButtonType[] {
            new ButtonType("Update", ButtonBar.ButtonData.OK_DONE),
            new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)};

    public static final ButtonType[] OK_CANCEL_BUTTONS = new ButtonType[] {
            new ButtonType("OK", ButtonBar.ButtonData.OK_DONE),
            new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)};

}
