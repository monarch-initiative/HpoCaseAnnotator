package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;

class SelectableOntologyTreeCell extends TreeCell<SelectableOntologyTreeTerm> {

    private final TermSelectionButton selectionButton;

    private ObservableValue<Boolean> includedProperty;
    private ObservableValue<Boolean> excludedProperty;

    SelectableOntologyTreeCell() {
        selectionButton = new TermSelectionButton();
    }

    static SelectableOntologyTreeCell of() {
        return new SelectableOntologyTreeCell();
    }

    @Override
    protected void updateItem(SelectableOntologyTreeTerm item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setText(getItem().term().getName());
            setGraphic(selectionButton);

            if (includedProperty != null)
                selectionButton.includedProperty().unbindBidirectional((BooleanProperty) includedProperty);
            if (excludedProperty != null)
                selectionButton.excludedProperty().unbindBidirectional((BooleanProperty) excludedProperty);

            includedProperty = item.includedProperty();
            selectionButton.includedProperty().bindBidirectional((Property<Boolean>) includedProperty);
            excludedProperty = item.excludedProperty();
            selectionButton.excludedProperty().bindBidirectional((Property<Boolean>) excludedProperty);

        }
    }

}

