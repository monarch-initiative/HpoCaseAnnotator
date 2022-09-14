package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;

/**
 * This base class is an {@link ObservableDataComponent} and also a {@link VBox}. The base class manages an
 * {@link ObjectProperty} with {@link T} that represents model object managed by the instance.
 *
 * @param <T> type of observable data.
 */
public abstract class VBoxObservableDataComponent<T> extends VBox implements ObservableDataComponent<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    protected abstract void initialize();

}
