package org.monarchinitiative.hpo_case_annotator.core.reference;

import org.monarchinitiative.phenol.ontology.data.TermId;

public record GeneIdentifier(TermId termId, String symbol) {

}
