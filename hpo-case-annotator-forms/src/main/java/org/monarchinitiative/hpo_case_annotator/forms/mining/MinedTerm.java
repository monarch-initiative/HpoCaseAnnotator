package org.monarchinitiative.hpo_case_annotator.forms.mining;

import org.monarchinitiative.phenol.ontology.data.Identified;

/**
 * Single named entity identified by text mining.
 */
public interface MinedTerm extends Identified {

    /**
     * @return start coordinate (included) of the term in the source text.
     */
    int start();

    /**
     * @return end coordinate (excluded) of the term in the source text.
     */
    int end();

    /**
     * @return <code>true</code> if the term was negated/excluded (e.g. not hypertension) in the source text.
     */
    boolean isNegated();

}
