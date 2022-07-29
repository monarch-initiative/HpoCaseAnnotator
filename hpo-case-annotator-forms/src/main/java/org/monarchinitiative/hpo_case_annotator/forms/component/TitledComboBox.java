package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class TitledComboBox<T> extends TitledBase<ComboBox<T>> {

    @Override
    protected ComboBox<T> getItem() {
        return new ComboBox<>();
    }

    public void setValue(T value) {
        item.setValue(value);
    }

    public T getValue() {
        return item.getValue();
    }

    public ObjectProperty<T> valueProperty() {
        return item.valueProperty();
    }

    public ObservableList<T> getItems() {
        return item.getItems();
    }

}
