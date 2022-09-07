package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

public class ObservableIndividual extends BaseObservableIndividual implements Individual {

    public ObservableIndividual() {
    }

    public ObservableIndividual(Individual individual) {
        super(individual);
    }
}
