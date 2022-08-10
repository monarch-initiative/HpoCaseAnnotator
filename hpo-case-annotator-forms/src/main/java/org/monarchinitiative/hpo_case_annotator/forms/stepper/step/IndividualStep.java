package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

public class IndividualStep<T extends ObservableIndividualStudy> extends BaseStep<T> {

    public IndividualStep() {
        super(IndividualStep.class.getResource("IndividualStep.fxml"));
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        // TODO - implement
    }

    @Override
    protected void unbind(T data) {
        // TODO - implement
    }
}
