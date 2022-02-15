package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import java.util.List;

public record FunctionalAnnotation(
        String geneSymbol,
        String txAccession,
        List<String> effect,
        String nucleotideAnnotation,
        String proteinAnnotation) {
}
