package org.monarchinitiative.hpo_case_annotator.observable.util;

import javafx.beans.InvalidationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ListenerHelper} implementation backed by an {@link ArrayList}.
 */
class ListenerHelperDefault implements ListenerHelper {

    // TODO - this is not necessarily the most memory-efficient implementation.
    //  Perhaps we can improve? See `com.sun.javafx.binding.ExpressionHelper` for some ideas.
    private final List<InvalidationListener> listeners;

    ListenerHelperDefault() {
        this.listeners = new ArrayList<>();
    }

    @Override
    public boolean addListener(InvalidationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(InvalidationListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public Iterable<InvalidationListener> listeners() {
        return listeners;
    }
}
