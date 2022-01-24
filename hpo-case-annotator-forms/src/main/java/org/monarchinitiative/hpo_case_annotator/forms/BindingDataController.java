package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

public abstract class BindingDataController<T> implements DataController<T> {

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
