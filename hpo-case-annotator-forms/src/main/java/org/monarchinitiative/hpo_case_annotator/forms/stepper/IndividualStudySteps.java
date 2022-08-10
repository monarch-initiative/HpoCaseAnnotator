package org.monarchinitiative.hpo_case_annotator.forms.stepper;

import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.IndividualStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.PublicationStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.VariantsStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividualStudy;

import java.util.List;

public class IndividualStudySteps {

    private final HCAControllerFactory controllerFactory;

    public IndividualStudySteps(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public List<Step<ObservableIndividualStudy>> getSteps() {
        VariantsStep<ObservableIndividualStudy> variants = new VariantsStep<>(controllerFactory);
        variants.setHeader("Add genomic variants identified in the individual.");
        return List.of(
                new PublicationStep<>(),
                variants,
                new IndividualStep<>(),
                new IdStep<>()
        );
    }

}
