package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

// Roughly corresponds to PhenotypicFeature of Phenopackets v2
public interface PhenotypicFeature extends Identified {

    static PhenotypicFeature of(TermId termId, String label, boolean isExcluded, TimeElement onset, TimeElement resolution) {
        return new PhenotypicFeatureDefault(termId, label, isExcluded, onset, resolution);
    }

    @Deprecated(forRemoval = true)
    default TermId termId() {
        return id();
    }

    String getLabel();

    boolean isExcluded();

    TimeElement getOnset();

    TimeElement getResolution();

    @Deprecated(forRemoval = true)
    default AgeRange getObservationAge() {
        throw new RuntimeException("Should not be used");
    }

    // TODO - we may add other attributes like severity, onset/offset

    int hashCode();

    boolean equals(Object o);

    default boolean isIncluded() {
        return !isExcluded();
    }
}
