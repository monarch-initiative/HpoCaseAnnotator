package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public class IdStep<T extends ObservableStudy> extends BaseStep<T> {

    public IdStep() {
        super(IdStep.class.getResource("IdStep.fxml"));
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
