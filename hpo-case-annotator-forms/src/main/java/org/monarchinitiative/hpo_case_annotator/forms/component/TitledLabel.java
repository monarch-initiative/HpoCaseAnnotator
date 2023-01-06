package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.util.List;

public class TitledLabel extends TitledBase<Label> {

    private static final List<String> STYLECLASSES = List.of("tl-label");

    @Override
    protected Label createTitledItem() {
        return new Label();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public void setText(String text) {
        titledItem.setText(text);
    }

    public String getText() {
        return titledItem.getText();
    }

    public StringProperty textProperty() {
        return titledItem.textProperty();
    }

}
