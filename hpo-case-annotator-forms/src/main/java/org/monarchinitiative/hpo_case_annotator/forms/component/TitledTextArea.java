package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

import java.util.List;

public class TitledTextArea extends TitledBase<TextArea> {

    private static final List<String> STYLECLASSES = List.of("tl-text-area");
    @Override
    protected TextArea createTitledItem() {
        return new TextArea();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public void setText(String value) {
        titledItem.setText(value);
    }

    public String getText() {
        return titledItem.getText();
    }

    public StringProperty textProperty() {
        return titledItem.textProperty();
    }

    public void setPromptText(String value) {
        titledItem.setPromptText(value);
    }

    public String getPromptText() {
        return titledItem.getPromptText();
    }

    public StringProperty promptTextProperty() {
        return titledItem.promptTextProperty();
    }

}
