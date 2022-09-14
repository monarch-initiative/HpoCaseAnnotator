package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;

import java.util.stream.Stream;

public class ObservableIndividual extends BaseObservableIndividual implements Individual {

    public static final Callback<ObservableIndividual, Observable[]> EXTRACTOR = BaseObservableIndividual.EXTRACTOR::call;

    public ObservableIndividual() {
    }

    public ObservableIndividual(Individual individual) {
        super(individual);
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return super.objectProperties(); // Nothing on top of the parent
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this)); // Nothing on top of the parent
    }

}
