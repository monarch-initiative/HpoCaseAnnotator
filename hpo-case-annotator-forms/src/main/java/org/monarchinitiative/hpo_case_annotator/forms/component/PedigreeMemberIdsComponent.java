package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

import static javafx.beans.binding.Bindings.*;

public class PedigreeMemberIdsComponent extends BaseIndividualIdsComponent<ObservablePedigreeMember> {

    @FXML
    private TitledLabel paternalId;
    @FXML
    private TitledLabel maternalId;
    @FXML
    private TitledLabel proband;

    public PedigreeMemberIdsComponent() {
        super(PedigreeMemberIdsComponent.class.getResource("PedigreeMemberIdsComponent.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(ObservablePedigreeMember data) {
        super.bind(data);
        if (data != null) {
            paternalId.textProperty().bind(data.paternalIdProperty());
            maternalId.textProperty().bind(data.maternalIdProperty());
            proband.textProperty().bind(
                    when(selectBoolean(data, "proband"))
                            .then("Yes")
                            .otherwise("No"));
        }
    }

    @Override
    protected void unbind(ObservablePedigreeMember data) {
        super.unbind(data);
        if (data != null) {
            paternalId.textProperty().unbind();
            maternalId.textProperty().unbind();
            proband.textProperty().unbind();
        }
    }

}
