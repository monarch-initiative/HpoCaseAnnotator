package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PedigreeMemberTitle extends HBox {

    @FXML
    ImageView icon;
    @FXML
    Label probandId;
    @FXML
    Label summary;

    public PedigreeMemberTitle() {
        FXMLLoader loader = new FXMLLoader(PedigreeMemberTitle.class.getResource("PedigreeMemberTitle.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
