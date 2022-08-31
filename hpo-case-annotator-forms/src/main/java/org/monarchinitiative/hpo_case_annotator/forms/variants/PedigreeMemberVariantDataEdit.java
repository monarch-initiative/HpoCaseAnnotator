package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

public class PedigreeMemberVariantDataEdit extends VariantDataEdit<ObservablePedigreeMember> {

    public PedigreeMemberVariantDataEdit() {
        FXMLLoader loader = new FXMLLoader(PedigreeMemberVariantDataEdit.class.getResource("PedigreeMemberVariantDataEdit.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
