package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import java.util.List;

public interface FunctionalAnnotation {

    static FunctionalAnnotation of(String geneSymbol,
                                   String txAccession,
                                   List<String> effect,
                                   String nucleotideAnnotation,
                                   String proteinAnnotation) {
        return new FunctionalAnnotationDefault(geneSymbol, txAccession, effect, nucleotideAnnotation, proteinAnnotation);
    }

    String geneSymbol();

    String txAccession();

    List<String> effect();

    String nucleotideAnnotation();

    String proteinAnnotation();

}
