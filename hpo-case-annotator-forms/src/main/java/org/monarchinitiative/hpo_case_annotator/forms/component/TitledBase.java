package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

abstract class TitledBase<T extends Region> extends VBox {

    static final String STYLESHEET_LOCATION = Objects.requireNonNull(TitledBase.class.getResource("titled-base.css")).toString();
    private static final String BOX_STYLE_CLASS = "tl-box";
    private static final String NAME_STYLE_CLASS = "tl-name";

    private final Label name = new Label();
    protected final T titledItem = createTitledItem();

    TitledBase() {
        getStylesheets().add(STYLESHEET_LOCATION);
        getStyleClass().add(BOX_STYLE_CLASS);
        name.getStyleClass().add(NAME_STYLE_CLASS);
        titledItem.getStyleClass().addAll(itemStyleClasses());
        HBox.setHgrow(titledItem, Priority.ALWAYS);

        getChildren().addAll(name, titledItem);

        titledItem.prefWidthProperty().bind(prefWidthProperty());
        titledItem.minWidthProperty().bind(name.widthProperty());
    }

    protected abstract T createTitledItem();

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

    public T getTitledItem() {
        return titledItem;
    }
}
