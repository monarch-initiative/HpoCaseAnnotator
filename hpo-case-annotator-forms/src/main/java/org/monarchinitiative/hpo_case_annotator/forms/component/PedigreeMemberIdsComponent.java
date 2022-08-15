package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import java.io.IOException;

import static javafx.beans.binding.Bindings.selectBoolean;
import static javafx.beans.binding.Bindings.when;

public class PedigreeMemberIdsComponent extends BaseIndividualIdsComponent<ObservablePedigreeMember> {

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

    @Override
    protected void bind(ObservablePedigreeMember data) {
        super.bind(data);
        paternalId.textProperty().bind(data.paternalIdProperty());
        maternalId.textProperty().bind(data.maternalIdProperty());
        proband.textProperty().bind(
                when(selectBoolean(data, "proband"))
                        .then("Yes")
                        .otherwise("No"));
    }

    @Override
    protected void unbind(ObservablePedigreeMember data) {
        super.unbind(data);
        paternalId.textProperty().unbind();
        maternalId.textProperty().unbind();
        proband.textProperty().unbind();
    }

}
