package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;

public abstract class BindingObservableDataComponent<T> implements ObservableDataComponent<T> {

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
