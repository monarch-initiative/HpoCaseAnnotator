package org.monarchinitiative.hpo_case_annotator.core.mining;

import java.util.List;

record TextMiningResultsDefault(String sourceText, List<MinedTerm> minedTerms) implements TextMiningResults {

}
