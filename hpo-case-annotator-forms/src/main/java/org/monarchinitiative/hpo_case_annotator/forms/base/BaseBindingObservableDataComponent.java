package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class BaseBindingObservableDataComponent<T> extends BindingObservableDataComponent<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }
}
