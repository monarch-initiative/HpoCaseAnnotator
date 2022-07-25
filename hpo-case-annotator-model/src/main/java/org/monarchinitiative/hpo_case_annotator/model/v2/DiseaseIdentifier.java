package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

public interface DiseaseIdentifier extends Identified {

    static DiseaseIdentifier of(TermId diseaseId, String diseaseName) {
        return new DiseaseIdentifierDefault(diseaseId, diseaseName);
    }

    /**
     * @return term id with disease ID, i.e. <code>OMIM:154700</code>, or <code>ORPHA:558</code>
     * @deprecated use {@link #id()} instead.
     */
    @Deprecated(forRemoval = true)
    default TermId getDiseaseId() {
        return id();
    }

    /**
     * @return human readable disease name, i.e. <code>Marfan syndrome</code>
     */
    String getDiseaseName();


}
