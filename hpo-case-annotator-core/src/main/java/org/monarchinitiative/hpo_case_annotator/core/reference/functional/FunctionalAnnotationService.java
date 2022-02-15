package org.monarchinitiative.hpo_case_annotator.core.reference.functional;

import org.monarchinitiative.svart.GenomicVariant;

import java.util.List;

public interface FunctionalAnnotationService {

    List<FunctionalAnnotation> annotate(GenomicVariant variant);

}
