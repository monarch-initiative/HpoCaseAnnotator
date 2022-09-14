package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record PhenotypicFeatureDefault(TermId termId, String label, boolean isExcluded, TimeElement onset, TimeElement resolution) implements PhenotypicFeature {
    @Override
    public TermId id() {
        return termId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public TimeElement getOnset() {
        return onset;
    }

    @Override
    public TimeElement getResolution() {
        return resolution;
    }
}
