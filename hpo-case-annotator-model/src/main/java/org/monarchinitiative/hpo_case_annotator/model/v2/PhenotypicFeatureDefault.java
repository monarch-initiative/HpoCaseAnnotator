package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record PhenotypicFeatureDefault(TermId termId, boolean isExcluded) implements PhenotypicFeature {
}
