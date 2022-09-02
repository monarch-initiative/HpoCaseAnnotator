package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCohortStudy;

public class CohortStudyIdStep extends BaseStudyIdStep<ObservableCohortStudy> {

    public CohortStudyIdStep() {
        super(CohortStudyIdStep.class.getResource("CohortStudyIdStep.fxml"));
    }

}
