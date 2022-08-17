package org.monarchinitiative.hpo_case_annotator.forms.disease;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class PedigreeMemberDiseaseDataEdit extends DiseaseDataEdit<ObservablePedigreeMember> {

    public PedigreeMemberDiseaseDataEdit() {
        FXMLLoader loader = new FXMLLoader(PedigreeMemberDiseaseDataEdit.class.getResource("PedigreeMemberDiseaseDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
