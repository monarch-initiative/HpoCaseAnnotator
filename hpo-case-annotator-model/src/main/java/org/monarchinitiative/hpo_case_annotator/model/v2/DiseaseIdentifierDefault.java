package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record DiseaseIdentifierDefault(TermId diseaseId,
                                String diseaseName) implements DiseaseIdentifier {
    @Override
    public TermId id() {
        return diseaseId;
    }

    @Override
    public String getDiseaseName() {
        return diseaseName;
    }
}
