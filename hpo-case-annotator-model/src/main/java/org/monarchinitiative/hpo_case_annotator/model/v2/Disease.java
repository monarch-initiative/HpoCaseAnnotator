package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

public interface Disease {

    static Disease of(TermId diseaseId, String diseaseName, boolean isExcluded) {
        return new DiseaseDefault(diseaseId, diseaseName, isExcluded);
    }

    /**
     * @return term id with disease ID, i.e. <code>OMIM:154700</code>, or <code>ORPHA:558</code>
     */
    TermId diseaseId();

    /**
     * @return human readable disease name, i.e. <code>Marfan syndrome</code>
     */
    String diseaseName();

    /**
     * @return <code>true</code> if presence of the disease was excluded
     */
    boolean isExcluded();

    default boolean isPresent() {
        return !isExcluded();
    }

}
