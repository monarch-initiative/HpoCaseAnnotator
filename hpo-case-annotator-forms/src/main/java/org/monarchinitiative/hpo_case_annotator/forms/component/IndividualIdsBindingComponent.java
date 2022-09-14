package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.util.stream.Stream;

public class IndividualIdsBindingComponent extends BaseIndividualIdsBindingComponent<ObservableIndividual> {

    static final Callback<IndividualIdsBindingComponent, Stream<Observable>> EXTRACTOR = tbc -> Stream.of(
            // Intentionally blank.
    );

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
            valueIsNotBeingSetByUserInteraction = true;
            super.bind(data);
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    @Override
    protected void unbind(ObservableIndividual data) {
        super.unbind(data);
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.concat(
                BaseIndividualIdsBindingComponent.EXTRACTOR.call(this),
                EXTRACTOR.call(this)
        );
    }
}
