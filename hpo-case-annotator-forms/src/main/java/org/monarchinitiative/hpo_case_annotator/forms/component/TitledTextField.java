package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.List;

public class TitledTextField extends TitledBase<TextField> {

    private static final List<String> STYLECLASSES = List.of("tl-text-field");

    @Override
    protected TextField createTitledItem() {
        return new TextField();
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

    public StringProperty promptTextProperty() {
        return titledItem.promptTextProperty();
    }

    public String getPromptText() {
        return titledItem.getPromptText();
    }

    public void setPromptText(String value) {
        titledItem.setPromptText(value);
    }

    public TextFormatter<?> getTextFormatter() {
        return titledItem.getTextFormatter();
    }

    public void setTextFormatter(TextFormatter<?> formatter) {
        titledItem.setTextFormatter(formatter);
    }

    public ObjectProperty<TextFormatter<?>> textFormatterProperty() {
        return titledItem.textFormatterProperty();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return titledItem.onActionProperty();
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return titledItem.getOnAction();
    }

    public void setOnAction(EventHandler<ActionEvent> handler) {
        titledItem.setOnAction(handler);
    }
}
