package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class PedigreeMemberIdsComponent extends BaseIndividualIdsComponent {

    @FXML
    private TitledLabel paternalId;
    @FXML
    private TitledLabel maternalId;
    @FXML
    private TitledLabel proband;

    public PedigreeMemberIdsComponent() {
        FXMLLoader loader = new FXMLLoader(PedigreeMemberIdsComponent.class.getResource("PedigreeMemberIdsComponent.fxml"));
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

    public StringProperty paternalIdProperty() {
        return paternalId.textProperty();
    }

    public StringProperty maternalIdProperty() {
        return maternalId.textProperty();
    }

    public StringProperty probandProperty() {
        return proband.textProperty();
    }

}
