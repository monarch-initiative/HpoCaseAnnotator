package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record PhenotypicFeatureDefault(TermId termId, boolean isExcluded, AgeRange observationAge) implements PhenotypicFeature {
    @Override
    public TermId id() {
        return termId;
    }

    @Override
    public AgeRange getObservationAge() {
        return observationAge;
    }
}
