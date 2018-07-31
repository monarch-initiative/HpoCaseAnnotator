package org.monarchinitiative.hpo_case_annotator.util.controller;


import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated
public class PopUpWindow {

    protected Logger log = LogManager.getLogger(PopUpWindow.class.getName());

    /* PopUp Window creates a new Stage (what's actually a window).
     * We keep a reference to the Stage here for e.g. allowing
     * to close it with a Button click. */
    protected Stage window;


    public void setWindow(Stage window) {
        this.window = window;
    }

}
