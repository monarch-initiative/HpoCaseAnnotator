package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberIdsBindingComponent extends BaseIndividualIdsBindingComponent<ObservablePedigreeMember> {

    @FXML
    private TitledTextField paternalId;
    @FXML
    private TitledTextField maternalId;
    @FXML
    private TitledCheckBox proband;

    public PedigreeMemberIdsBindingComponent() {
        super(PedigreeMemberIdsBindingComponent.class.getResource("PedigreeMemberIdsBindingComponent.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(ObservablePedigreeMember data) {
        try {
            valueIsBeingSetProgrammatically = true;
            super.bind(data);
            if (data != null) {
                paternalId.textProperty().bindBidirectional(data.paternalIdProperty());
                maternalId.textProperty().bindBidirectional(data.maternalIdProperty());
                proband.selectedProperty().bindBidirectional(data.probandProperty());
            } else {
                paternalId.setText(null);
                maternalId.setText(null);
                proband.setSelected(false);
            }
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(ObservablePedigreeMember data) {
        super.unbind(data);
        if (data != null) {
            paternalId.textProperty().unbindBidirectional(data.paternalIdProperty());
            maternalId.textProperty().unbindBidirectional(data.maternalIdProperty());
            proband.selectedProperty().unbindBidirectional(data.probandProperty());
        }
    }
}
