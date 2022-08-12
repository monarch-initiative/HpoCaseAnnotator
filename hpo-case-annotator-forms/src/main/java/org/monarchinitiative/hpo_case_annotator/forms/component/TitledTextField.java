package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.List;

public class TitledTextField extends TitledBase<TextField> {

    private static final List<String> STYLECLASSES = List.of("tl-text-field");

    @Override
    protected TextField getItem() {
        return new TextField();
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
