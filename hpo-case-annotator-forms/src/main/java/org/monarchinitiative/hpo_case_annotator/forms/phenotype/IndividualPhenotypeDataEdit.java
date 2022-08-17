package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.io.IOException;

public class IndividualPhenotypeDataEdit extends PhenotypeDataEdit<ObservableIndividual> {

    public IndividualPhenotypeDataEdit() {
        FXMLLoader loader = new FXMLLoader(PhenotypeDataEdit.class.getResource("IndividualPhenotypeDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
