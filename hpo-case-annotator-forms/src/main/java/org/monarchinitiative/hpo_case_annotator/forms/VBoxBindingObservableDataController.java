package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Very frequently we need an {@link ObservableDataController} that is also a {@link VBox}. This base class handles
 * setting listeners to the {@link #dataProperty()}.
 *
 * @param <T> type of observable data.
 */
public abstract class VBoxBindingObservableDataController<T> extends VBoxObservableDataController<T> {

    @FXML
    @Override
    protected void initialize() {
        dataProperty().addListener(onDataChange());
    }

    private ChangeListener<T> onDataChange() {
        return (obs, old, novel) -> {
            // TODO - this may cause issues downstream
//            if (old != null)
                unbind(old);
//            if (novel != null)
                bind(novel);
        };
    }

    protected abstract void bind(T data);

    protected abstract void unbind(T data);
}
