package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id.BaseIndividuaIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id.IndividualIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype.BasePhenotypeStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype.IndividualPhenotypeStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualSteps extends BaseIndividualSteps<ObservableIndividual> {

    @Override
    protected BasePhenotypeStep<ObservableIndividual> getPhenotypeStep() {
        return new IndividualPhenotypeStep();
    }

    @Override
    public IndividualSteps configureSteps() {
        super.configureSteps();
        return this;
    }

    @Override
    protected BaseIndividuaIdStep<ObservableIndividual> getIdStep() {
        return new IndividualIdStep();
    }
}
