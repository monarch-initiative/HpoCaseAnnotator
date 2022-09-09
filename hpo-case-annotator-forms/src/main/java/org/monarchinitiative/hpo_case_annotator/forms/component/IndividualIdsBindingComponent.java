package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualIdsBindingComponent extends BaseIndividualIdsBindingComponent<ObservableIndividual> {

    public IndividualIdsBindingComponent() {
        super(IndividualIdsBindingComponent.class.getResource("IndividualIdsBindingComponent.fxml"));
    }
    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(ObservableIndividual data) {
        try {
            valueIsBeingSetProgrammatically = true;
            super.bind(data);
        } finally {
            valueIsBeingSetProgrammatically = false;
        }
    }

    @Override
    protected void unbind(ObservableIndividual data) {
        super.unbind(data);
    }
}
