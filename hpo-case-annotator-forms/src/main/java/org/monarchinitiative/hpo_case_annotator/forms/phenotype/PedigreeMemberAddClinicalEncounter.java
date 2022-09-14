package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberAddClinicalEncounter extends BaseAddClinicalEncounter<ObservablePedigreeMember> {

    public PedigreeMemberAddClinicalEncounter() {
        super(PedigreeMemberAddClinicalEncounter.class.getResource("PedigreeMemberAddClinicalEncounter.fxml"));
    }
}
