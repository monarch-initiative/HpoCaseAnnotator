package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.List;

public class TitledComboBox<T> extends TitledBase<ComboBox<T>> {

    private static final List<String> STYLECLASSES = List.of("tl-combo-box");

    @Override
    protected ComboBox<T> createItem() {
        return new ComboBox<>();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
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
