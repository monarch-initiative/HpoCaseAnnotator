package org.monarchinitiative.hpo_case_annotator.core.mining;

import java.util.List;

/**
 * A container of text mining results.
 */
public interface TextMiningResults {

    static TextMiningResults of(String sourceText, List<MinedTerm> minedTerms) {
        return new TextMiningResultsDefault(sourceText, minedTerms);
    }

    /**
     * @return string with the source text that underwent text mining.
     */
    String sourceText();

    /**
     * @return a list of terms identified in text mining.
     */
    List<MinedTerm> minedTerms();

}
