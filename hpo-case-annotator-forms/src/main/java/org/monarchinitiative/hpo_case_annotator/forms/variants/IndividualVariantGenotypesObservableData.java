package org.monarchinitiative.hpo_case_annotator.forms.variants;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualVariantGenotypesObservableData extends VariantGenotypesObservableData<ObservableIndividual> {

    public IndividualVariantGenotypesObservableData() {
        super(IndividualVariantGenotypesObservableData.class.getResource("IndividualVariantGenotypesObservableData.fxml"));
    }
}
