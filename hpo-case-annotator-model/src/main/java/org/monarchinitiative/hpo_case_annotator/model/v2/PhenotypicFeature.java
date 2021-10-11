package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

// Corresponds to PhenotypicFeature of Phenopackets v2
public interface PhenotypicFeature {

    static PhenotypicFeature of(TermId termId, boolean isExcluded) {
        return new PhenotypicFeatureDefault(termId, isExcluded);
    }

    TermId termId();

    boolean isExcluded();

    // TODO - we may add other attributes like severity, onset

    int hashCode();

    boolean equals(Object o);
}
