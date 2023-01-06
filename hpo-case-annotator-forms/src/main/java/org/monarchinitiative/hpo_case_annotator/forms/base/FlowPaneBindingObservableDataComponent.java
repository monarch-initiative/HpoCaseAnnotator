package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

public abstract class FlowPaneBindingObservableDataComponent<T> extends FlowPaneObservableDataComponent<T> {

    @FXML
    protected void initialize() {
        data.addListener(onDataChange());
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
