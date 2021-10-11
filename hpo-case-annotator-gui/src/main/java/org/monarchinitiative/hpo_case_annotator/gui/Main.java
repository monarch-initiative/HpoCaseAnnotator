package org.monarchinitiative.hpo_case_annotator.gui;


/**
 * The driver class of the Hpo Case Annotator app.
 */
public class Main {

    public static void main(String[] args) {
        // Until we are fully modular, we must trick JRE by calling the main method of the other app
        App.main(args);
    }

}
