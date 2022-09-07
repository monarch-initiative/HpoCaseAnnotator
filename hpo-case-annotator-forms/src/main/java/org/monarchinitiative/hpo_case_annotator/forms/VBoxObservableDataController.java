package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * This base class is an {@link ObservableDataController} and also a {@link VBox}. The base class manages an
 * {@link ObjectProperty} with {@link T} that represents model object managed by the instance.
 *
 * @param <T> type of observable data.
 */
// TODO - remove Controller from the name
public abstract class VBoxObservableDataController<T> extends VBox implements ObservableDataController<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    protected abstract void initialize();

}
