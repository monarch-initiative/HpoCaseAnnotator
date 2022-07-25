package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

// Roughly corresponds to PhenotypicFeature of Phenopackets v2
public interface PhenotypicFeature extends Identified {

    static PhenotypicFeature of(TermId termId, boolean isExcluded, AgeRange observationAge) {
        return new PhenotypicFeatureDefault(termId, isExcluded, observationAge);
    }

    @Deprecated(forRemoval = true)
    default TermId termId() {
        return id();
    }

    boolean isExcluded();

    AgeRange getObservationAge();

    // TODO - we may add other attributes like severity, onset/offset

    int hashCode();

    boolean equals(Object o);

    default boolean isIncluded() {
        return !isExcluded();
    }
}
