package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

public class ObservableIndividual extends BaseObservableIndividual implements Individual {

    public ObservableIndividual() {
    }

    public ObservableIndividual(Individual individual) {
        super(individual);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this)) {
            observable.addListener(listener);
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this)) {
            observable.removeListener(listener);
        }
    }
}
