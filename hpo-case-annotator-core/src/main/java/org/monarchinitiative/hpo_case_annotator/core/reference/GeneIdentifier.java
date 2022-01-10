package org.monarchinitiative.hpo_case_annotator.core.reference;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

public record GeneIdentifier(TermId termId, String symbol) {

    public static GeneIdentifier of(TermId termId, String symbol) {
        return new GeneIdentifier(termId, symbol);
    }

    public GeneIdentifier(TermId termId, String symbol) {
        this.termId = Objects.requireNonNull(termId, "Term ID must not be null");
        this.symbol = Objects.requireNonNull(symbol, "Symbol must not be null");
    }

}
