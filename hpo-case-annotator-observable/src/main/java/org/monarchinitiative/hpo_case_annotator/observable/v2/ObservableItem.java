package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.stream.Stream;

/**
 * A little convenience class for implementing {@link Observable} based on {@link #observables()}.
 */
abstract class ObservableItem implements Observable {

    protected abstract Stream<Observable> observables();

    @Override
    public void addListener(InvalidationListener listener) {
        observables().forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observables().forEach(obs -> obs.removeListener(listener));
    }
}
