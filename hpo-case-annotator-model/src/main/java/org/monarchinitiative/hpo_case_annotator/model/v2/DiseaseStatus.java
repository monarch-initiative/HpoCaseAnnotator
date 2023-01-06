package org.monarchinitiative.hpo_case_annotator.model.v2;

// TODO - use OntologyClass instead of DiseaseIdentifier
public interface DiseaseStatus {

    static DiseaseStatus of(DiseaseIdentifier diseaseId, boolean isExcluded) {
        return new DiseaseStatusDefault(diseaseId, isExcluded);
    }

    DiseaseIdentifier getDiseaseId();

    /**
     * @return <code>true</code> if presence of the disease was excluded
     */
    boolean isExcluded();

    default boolean isPresent() {
        return !isExcluded();
    }

}
