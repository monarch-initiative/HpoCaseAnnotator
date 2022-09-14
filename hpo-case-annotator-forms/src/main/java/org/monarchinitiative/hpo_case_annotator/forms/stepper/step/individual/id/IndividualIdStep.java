package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualIdStep extends BaseIndividuaIdStep<ObservableIndividual> {

    @FXML
    private IndividualIdsBindingComponent individualIds;

    public IndividualIdStep() {
        super(IndividualIdStep.class.getResource("IndividualIdStep.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
        individualIds.dataProperty().bind(data);
    }

}
