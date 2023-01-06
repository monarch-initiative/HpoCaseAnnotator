package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual;

import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id.BaseIndividuaIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.id.PedigreeMemberIdStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype.BasePhenotypeStep;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype.PedigreeMemberPhenotypeStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberSteps extends BaseIndividualSteps<ObservablePedigreeMember> {

    @Override
    protected BaseIndividuaIdStep<ObservablePedigreeMember> getIdStep() {
        return new PedigreeMemberIdStep();
    }

    @Override
    public PedigreeMemberSteps configureSteps() {
        super.configureSteps();
        return this;
    }

    @Override
    protected BasePhenotypeStep<ObservablePedigreeMember> getPhenotypeStep() {
        return new PedigreeMemberPhenotypeStep();
    }
}
