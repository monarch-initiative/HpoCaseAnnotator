package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

public class IndividualStudyIdStep extends BaseStudyIdStep<ObservableIndividualStudy> {

    public IndividualStudyIdStep() {
        super(IndividualStudyIdStep.class.getResource("IndividualStudyIdStep.fxml"));
    }

}
