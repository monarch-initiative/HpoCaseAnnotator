package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

public class ObservableIndividual extends BaseObservableIndividual implements Individual {

    public static ObservableIndividual defaultInstance() {
        ObservableIndividual instance = new ObservableIndividual();
        instance.setAge(ObservableTimeElement.defaultInstance());
        instance.setVitalStatus(ObservableVitalStatus.defaultInstance());
        return instance;
    }

    public ObservableIndividual() {
    }

    public ObservableIndividual(Individual individual) {
        super(individual);
    }
}
