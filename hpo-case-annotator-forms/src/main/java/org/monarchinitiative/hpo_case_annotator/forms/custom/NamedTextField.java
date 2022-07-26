package org.monarchinitiative.hpo_case_annotator.forms.custom;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

public class NamedTextField extends NamedBase<TextField> {

    @Override
    protected TextField getItem() {
        return new TextField();
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
