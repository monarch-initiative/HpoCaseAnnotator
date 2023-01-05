package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.study;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableFamilyStudy;

public class FamilyStudyIdStep extends BaseStudyIdStep<ObservableFamilyStudy> {

    public FamilyStudyIdStep() {
        super(FamilyStudyIdStep.class.getResource("FamilyStudyIdStep.fxml"));
    }

    @Override
    protected String generateId(ObservableFamilyStudy data) {
        return "family-id";
    }
}
