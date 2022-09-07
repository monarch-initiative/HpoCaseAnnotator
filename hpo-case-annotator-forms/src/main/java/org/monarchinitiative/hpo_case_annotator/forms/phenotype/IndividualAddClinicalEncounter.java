package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualAddClinicalEncounter extends BaseAddClinicalEncounter<ObservableIndividual> {

    public IndividualAddClinicalEncounter() {
        super(IndividualAddClinicalEncounter.class.getResource("IndividualAddClinicalEncounter.fxml"));
    }
}
