package org.monarchinitiative.hpo_case_annotator.forms.variants;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePedigreeMember;

public class PedigreeMemberVariantGenotypesObservableData extends VariantGenotypesObservableData<ObservablePedigreeMember> {

    public PedigreeMemberVariantGenotypesObservableData() {
        super(PedigreeMemberVariantGenotypesObservableData.class.getResource("PedigreeMemberVariantGenotypesObservableData.fxml"));
    }

}
