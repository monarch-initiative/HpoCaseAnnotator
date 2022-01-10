package org.monarchinitiative.hpo_case_annotator.forms.study;

public class ObservableIndividual extends BaseObservableIndividual {


    public ObservableIndividual() {
    }

    public ObservableIndividual(Builder builder) {
        super(builder);
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
