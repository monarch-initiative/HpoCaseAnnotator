package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

public class ObservableIndividual extends BaseObservableIndividual<Individual> implements Individual, Updateable<Individual> {

    public ObservableIndividual() {
    }

    public ObservableIndividual(Builder builder) {
        super(builder);
    }

    @Override
    public void update(Individual data) {
        super.update(data);
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
