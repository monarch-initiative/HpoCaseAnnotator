package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

import java.util.List;

public class TitledTextArea extends TitledBase<TextArea> {

    private static final List<String> STYLECLASSES = List.of("tl-text-area");
    @Override
    protected TextArea createItem() {
        return new TextArea();
    }

    @Override
    protected List<String> itemStyleClasses() {
        return STYLECLASSES;
    }

    public void setText(String value) {
        item.setText(value);
    }

    public String getText() {
        return item.getText();
    }

    public StringProperty textProperty() {
        return item.textProperty();
    }

    public void setPromptText(String value) {
        item.setPromptText(value);
    }

    public String getPromptText() {
        return item.getPromptText();
    }

    public StringProperty promptTextProperty() {
        return item.promptTextProperty();
    }

}
