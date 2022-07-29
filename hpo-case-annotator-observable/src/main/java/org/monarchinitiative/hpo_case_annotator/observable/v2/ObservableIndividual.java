package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

public class ObservableIndividual extends BaseObservableIndividual<Individual> implements Individual {

    public ObservableIndividual() {
    }

    public ObservableIndividual(Builder builder) {
        super(builder);
    }

    public ObservableIndividual(Individual individual) {
        super(individual);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseObservableIndividual.Builder<Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ObservableIndividual build() {
            return new ObservableIndividual(self());
        }
    }

}
