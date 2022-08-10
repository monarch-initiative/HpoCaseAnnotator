package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public class VariantsStep<T extends ObservableStudy> extends BaseStep<T> {

    public VariantsStep() {
        super(VariantsStep.class.getResource("VariantsStep.fxml"));
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
