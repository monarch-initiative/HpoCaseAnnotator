package org.monarchinitiative.hpo_case_annotator.model.v2;

record DiseaseStatusDefault(DiseaseIdentifier diseaseId,
                            boolean isExcluded) implements DiseaseStatus {
    @Override
    public DiseaseIdentifier getDiseaseId() {
        return diseaseId;
    }
}
