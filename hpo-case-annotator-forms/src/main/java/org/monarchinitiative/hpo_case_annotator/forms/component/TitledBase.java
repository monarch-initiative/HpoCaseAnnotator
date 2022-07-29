package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

abstract class TitledBase<T extends Control> extends VBox {

    static final String STYLESHEET_LOCATION = Objects.requireNonNull(TitledLabel.class.getResource("titled-base.css")).toString();

    private final Label name = new Label();
    protected final T item = getItem();

    TitledBase() {
        getStylesheets().add(STYLESHEET_LOCATION);
        getStyleClass().add("named-base");
        name.getStyleClass().add("named-base-name");
        item.getStyleClass().add("named-base-item");

        getChildren().addAll(name, item);

        item.prefWidthProperty().bind(prefWidthProperty());
        item.minWidthProperty().bind(name.widthProperty());
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
