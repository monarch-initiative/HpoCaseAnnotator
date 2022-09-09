package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.PedigreeMemberIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberIdStep extends BaseIndividuaIdStep<ObservablePedigreeMember> {

    @FXML
    private PedigreeMemberIdsBindingComponent individualIds;

    public PedigreeMemberIdStep() {
        super(PedigreeMemberIdStep.class.getResource("PedigreeMemberIdStep.fxml"));
    }


    @Override
    protected void initialize() {
        super.initialize();
        individualIds.dataProperty().bind(data);
    }

}
