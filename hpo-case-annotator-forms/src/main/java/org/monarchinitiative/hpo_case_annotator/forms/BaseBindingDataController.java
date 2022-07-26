package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class BaseBindingDataController<T> extends BindingDataController<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>(this, "data", defaultInstance());

    protected abstract T defaultInstance();

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }
}
