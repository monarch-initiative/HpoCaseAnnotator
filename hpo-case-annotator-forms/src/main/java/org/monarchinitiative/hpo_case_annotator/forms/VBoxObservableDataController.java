package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Very frequently we need an {@link ObservableDataController} that is also a {@link VBox}. This base class handles
 * setting listeners to the {@link #dataProperty()}.
 *
 * @param <T> type of observable data.
 */
public abstract class VBoxObservableDataController<T> extends VBox implements ObservableDataController<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    protected void initialize() {
        dataProperty().addListener(onDataChange());
    }

    private ChangeListener<T> onDataChange() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

    protected abstract void bind(T data);

    protected abstract void unbind(T data);
}
