package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class TitledTextField extends TitledBase<TextField> {

    @Override
    protected TextField getItem() {
        return new TextField();
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

    public StringProperty promptTextProperty() {
        return item.promptTextProperty();
    }

    public String getPromptText() {
        return item.getPromptText();
    }

    public void setPromptText(String value) {
        item.setPromptText(value);
    }

    public TextFormatter<?> getTextFormatter() {
        return item.getTextFormatter();
    }

    public void setTextFormatter(TextFormatter<?> formatter) {
        item.setTextFormatter(formatter);
    }

    public ObjectProperty<TextFormatter<?>> textFormatterProperty() {
        return item.textFormatterProperty();
    }
}
