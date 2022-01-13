package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;

/**
 * represents a case, either V1 or V2, or an observable component of the case.
 * @param <T>
 */
public interface DataController<T> {

    ObjectProperty<T> dataProperty();

    default T getData() {
        return dataProperty().get();
    }

    default void setData(T data) {
        dataProperty().set(data);
    }

}
