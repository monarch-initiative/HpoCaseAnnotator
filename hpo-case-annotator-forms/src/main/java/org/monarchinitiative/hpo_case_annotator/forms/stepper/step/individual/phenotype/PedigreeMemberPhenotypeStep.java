package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype;

import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BaseAddClinicalEncounter;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PedigreeMemberAddClinicalEncounter;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberPhenotypeStep extends BasePhenotypeStep<ObservablePedigreeMember> {

    @Override
    protected BaseAddClinicalEncounter<ObservablePedigreeMember> clinicalEncounterComponent() {
        return new PedigreeMemberAddClinicalEncounter();
    }

}
