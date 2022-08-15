package org.monarchinitiative.hpo_case_annotator.forms.mining;

import java.util.List;

/**
 * A container of text mining results.
 */
public interface TextMiningResults {

    /**
     * @return string with the source text that underwent text mining.
     */
    String sourceText();

    /**
     * @return a list of terms identified in text mining.
     */
    List<MinedTerm> minedTerms();

}
