package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import java.util.List;

/**
 * Default {@link FunctionalAnnotation} implementation backed by a record.
 */
record FunctionalAnnotationDefault(
        String geneSymbol,
        String txAccession,
        List<String> effect,
        String nucleotideAnnotation,
        String proteinAnnotation) implements FunctionalAnnotation {
}
