package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.component.IndividualIdsBindingComponent;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

public class IndividualStep<T extends ObservableIndividualStudy> extends BaseStep<T> {

    @FXML
    private IndividualIdsBindingComponent individualIds;

    public IndividualStep() {
        super(IndividualStep.class.getResource("IndividualStep.fxml"));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        individualIds.dataProperty().bindBidirectional(data.individualProperty());
    }

    @Override
    protected void unbind(T data) {
        individualIds.dataProperty().unbindBidirectional(data.individualProperty());
    }
}
