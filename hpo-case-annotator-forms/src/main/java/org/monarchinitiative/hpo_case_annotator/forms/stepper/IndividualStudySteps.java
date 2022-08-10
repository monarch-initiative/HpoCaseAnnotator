package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IndividualStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.PublicationStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.VariantsStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

import java.util.List;

public class IndividualStudySteps {

    public IndividualStudySteps() {
    }

    public List<Step<ObservableIndividualStudy>> getSteps() {
        return List.of(
                new PublicationStep<>(),
                new VariantsStep<>(),
                new IndividualStep<>(),
                new IdStep<>()
        );
    }

}
