package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.io.IOException;

public class IndividualVariantDataEdit extends VariantDataEdit<ObservableIndividual> {

    public IndividualVariantDataEdit() {
        FXMLLoader loader = new FXMLLoader(IndividualVariantDataEdit.class.getResource("IndividualVariantDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
