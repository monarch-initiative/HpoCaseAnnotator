package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id;

import javafx.beans.Observable;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;

import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseIndividuaIdStep<T extends BaseObservableIndividual> extends BaseStep<T> {

    protected BaseIndividuaIdStep(URL location) {
        super(location);
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of();
    }

    @Override
    public void invalidated(Observable observable) {

    }

    @Override
    protected void bind(T data) {

    }

    @Override
    protected void unbind(T data) {

    }
}
