package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record DiseaseDefault(TermId diseaseId, String diseaseName, boolean isExcluded) implements Disease {
}
