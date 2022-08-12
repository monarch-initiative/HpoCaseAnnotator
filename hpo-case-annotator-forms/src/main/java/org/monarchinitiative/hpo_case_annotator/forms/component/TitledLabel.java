package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.util.List;

public class TitledLabel extends TitledBase<Label> {

    private static final List<String> STYLECLASSES = List.of("tl-label");

    @Override
    protected Label getItem() {
        return new Label();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public void setText(String text) {
        item.setText(text);
    }

    public String getText() {
        return item.getText();
    }

    public StringProperty textProperty() {
        return item.textProperty();
    }

}
