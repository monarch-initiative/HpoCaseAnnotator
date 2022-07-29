package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class BaseBindingObservableDataController<T> extends BindingObservableDataController<T> {

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
