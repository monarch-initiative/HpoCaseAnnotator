package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.io.IOException;

public class IndividualDiseaseDataEdit extends DiseaseDataEdit<ObservableIndividual> {

    public IndividualDiseaseDataEdit() {
        FXMLLoader loader = new FXMLLoader(IndividualDiseaseDataEdit.class.getResource("IndividualDiseaseDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
