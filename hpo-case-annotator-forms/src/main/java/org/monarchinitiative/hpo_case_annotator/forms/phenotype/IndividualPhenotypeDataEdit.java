package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualPhenotypeDataEdit extends BasePhenotypeDataEdit<ObservableIndividual> {

    public IndividualPhenotypeDataEdit() {
        super(IndividualPhenotypeDataEdit.class.getResource("IndividualPhenotypeDataEdit.fxml"));
    }

    @Override
    protected BaseAddClinicalEncounter<ObservableIndividual> clinicalEncounterComponent() {
        return new IndividualAddClinicalEncounter();
    }
}
