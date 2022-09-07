package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.stream.Stream;

/**
 * An interface to help implement {@link Observable}.
 */
interface ObservableItem extends Observable {

    Stream<Observable> dependencies();

    @Override
    default void addListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.addListener(listener));
    }

    @Override
    default void removeListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.removeListener(listener));
    }
}
