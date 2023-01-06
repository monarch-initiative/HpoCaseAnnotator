package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;

/**
 * Very frequently we need an {@link ObservableDataComponent} that is also a {@link VBox}. This base class handles
 * setting listeners to the {@link #dataProperty()}.
 *
 * @param <T> type of observable data.
 */
public abstract class VBoxBindingObservableDataComponent<T> extends VBoxObservableDataComponent<T> {

    @FXML
    @Override
    protected void initialize() {
        dataProperty().addListener(onDataChange());
    }

    private ChangeListener<T> onDataChange() {
        return (obs, old, novel) -> {
            unbind(old);
            bind(novel);
        };
    }

    protected abstract void bind(T data);

    protected abstract void unbind(T data);
}
