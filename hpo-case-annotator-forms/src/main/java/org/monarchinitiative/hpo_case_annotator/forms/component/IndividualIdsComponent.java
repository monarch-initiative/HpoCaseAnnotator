package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

public class IndividualIdsComponent extends BaseIndividualIdsComponent<ObservableIndividual> {

    public IndividualIdsComponent() {
        super(IndividualIdsComponent.class.getResource("IndividualIdsComponent.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void bind(ObservableIndividual data) {
        super.bind(data);
    }

    @Override
    protected void unbind(ObservableIndividual data) {
        super.unbind(data);
    }

}
