package org.monarchinitiative.hpo_case_annotator.observable.util;

import javafx.beans.InvalidationListener;

public interface ListenerHelper {

    static ListenerHelper of() {
        return new ListenerHelperDefault();
    }

    boolean addListener(InvalidationListener listener);

    boolean removeListener(InvalidationListener listener);

    Iterable<InvalidationListener> listeners();

}
