package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;

import java.util.List;

public class TitledCheckBox extends TitledBase<CheckBox> {

    private static final List<String> STYLECLASSES = List.of("tl-check-box");

    @Override
    protected CheckBox getItem() {
        return new CheckBox();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public boolean isSelected() {
        return item.isSelected();
    }

    public void setSelected(boolean value) {
        item.setSelected(value);
    }

    public BooleanProperty selectedProperty() {
        return item.selectedProperty();
    }
}
