package org.monarchinitiative.hpo_case_annotator.forms.custom;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

public class NamedLabel extends NamedBase<Label> {

    @Override
    protected Label getItem() {
        return new Label();
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
