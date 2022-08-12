package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class IndividualIdsComponent extends BaseIndividualIdsComponent {

    public IndividualIdsComponent() {
        FXMLLoader loader = new FXMLLoader(IndividualIdsComponent.class.getResource("IndividualIdsComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

}
