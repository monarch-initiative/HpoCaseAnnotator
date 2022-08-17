package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class PedigreeMemberPhenotypeDataEdit extends PhenotypeDataEdit<ObservablePedigreeMember> {

    public PedigreeMemberPhenotypeDataEdit() {
        FXMLLoader loader = new FXMLLoader(PhenotypeDataEdit.class.getResource("PedigreeMemberPhenotypeDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
