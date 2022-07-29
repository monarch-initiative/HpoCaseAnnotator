package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;

public class TitledCheckBox extends TitledBase<CheckBox> {
    @Override
    protected CheckBox getItem() {
        return new CheckBox();
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
