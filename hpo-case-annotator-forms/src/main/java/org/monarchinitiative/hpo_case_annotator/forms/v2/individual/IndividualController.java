package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableIndividual;

public class IndividualController extends BaseIndividualController<ObservableIndividual> {

    private final ObjectProperty<ObservableIndividual> individual = new SimpleObjectProperty<>(this, "individual", new ObservableIndividual());

    public IndividualController() {
        super();
    }

    // simple "no-op" implementation

    @Override
    public ObjectProperty<ObservableIndividual> dataProperty() {
        return individual;
    }
}
