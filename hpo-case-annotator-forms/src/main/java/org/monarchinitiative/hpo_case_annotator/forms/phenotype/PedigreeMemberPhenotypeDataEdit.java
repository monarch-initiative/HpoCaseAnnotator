package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberPhenotypeDataEdit extends BasePhenotypeDataEdit<ObservablePedigreeMember> {

    public PedigreeMemberPhenotypeDataEdit() {
        super(PedigreeMemberPhenotypeDataEdit.class.getResource("PedigreeMemberPhenotypeDataEdit.fxml"));
    }

    @Override
    protected BaseAddClinicalEncounter<ObservablePedigreeMember> clinicalEncounterComponent() {
        return new PedigreeMemberAddClinicalEncounter();
    }
}
