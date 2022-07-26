package org.monarchinitiative.hpo_case_annotator.forms.custom;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public abstract class NamedBase<T extends Control> extends AnchorPane {

    public static final String STYLESHEET_LOCATION = Objects.requireNonNull(NamedLabel.class.getResource("named-base.css")).toString();

    private final Label name = new Label();
    protected final T item = getItem();

    public NamedBase() {
        getStylesheets().add(STYLESHEET_LOCATION);
        getStyleClass().add("named-label");
        name.getStyleClass().add("named-label-name");
        name.getStyleClass().add("named-label-text");

        getChildren().addAll(name, item);
        AnchorPane.setTopAnchor(name, 0.);
        AnchorPane.setLeftAnchor(name, 0.);
        AnchorPane.setTopAnchor(item, 12.);
        AnchorPane.setLeftAnchor(item, 5.);
    }

    protected abstract T getItem();

    public void setName(String name) {
        this.name.setText(name);
    }

    public String getName() {
        return name.getText();
    }

    public StringProperty nameProperty() {
        return name.textProperty();
    }

}
