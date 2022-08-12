package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

abstract class TitledBase<T extends Control> extends VBox {

    static final String STYLESHEET_LOCATION = Objects.requireNonNull(TitledBase.class.getResource("titled-base.css")).toString();

    private final Label name = new Label();
    protected final T item = getItem();

    TitledBase() {
        getStylesheets().add(STYLESHEET_LOCATION);
        name.getStyleClass().add("tl-name");
        item.getStyleClass().addAll(itemStyleClasses());

        getChildren().addAll(name, item);

        item.prefWidthProperty().bind(prefWidthProperty());
        item.minWidthProperty().bind(name.widthProperty());
    }

    protected abstract T getItem();

    protected abstract List<String> itemStyleClasses();

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
