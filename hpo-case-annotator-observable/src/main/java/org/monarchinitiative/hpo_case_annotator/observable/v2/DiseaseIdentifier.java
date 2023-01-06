package org.monarchinitiative.hpo_case_annotator.observable.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * A local {@link org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier} implementation with nullable
 * fields.
 */
public class DiseaseIdentifier implements org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier {

    private TermId termId;
    private String diseaseName;

    public DiseaseIdentifier() {
    }

    public DiseaseIdentifier(org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier diseaseIdentifier) {
        if (diseaseIdentifier != null) {
            termId = diseaseIdentifier.id();
            diseaseName = diseaseIdentifier.getDiseaseName();
        }
    }

    @Override
    public TermId id() {
        return termId;
    }

    public TermId getTermId() {
        return termId;
    }

    public void setTermId(TermId termId) {
        this.termId = termId;
    }

    @Override
    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    @Override
    public String toString() {
        return "DiseaseIdentifier{" +
                "termId=" + termId +
                ", diseaseName='" + diseaseName + '\'' +
                '}';
    }
}
