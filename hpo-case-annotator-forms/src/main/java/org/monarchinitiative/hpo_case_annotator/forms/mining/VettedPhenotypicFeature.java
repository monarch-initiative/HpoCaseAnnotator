package org.monarchinitiative.hpo_case_annotator.forms.mining;

import org.monarchinitiative.phenol.ontology.data.Identified;

/**
 * A phenotypic feature that is part of the results.
 */
public interface VettedPhenotypicFeature extends Identified {

    boolean isNegated();

    String getLabel();
}
