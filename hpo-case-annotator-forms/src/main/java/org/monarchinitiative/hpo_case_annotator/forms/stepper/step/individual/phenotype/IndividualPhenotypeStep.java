package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype;

import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BaseAddClinicalEncounter;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.IndividualAddClinicalEncounter;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualPhenotypeStep extends BasePhenotypeStep<ObservableIndividual> {

    @Override
    protected BaseAddClinicalEncounter<ObservableIndividual> clinicalEncounterComponent() {
        return new IndividualAddClinicalEncounter();
    }

}
