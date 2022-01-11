package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TreeCell;

class SelectableOntologyTreeCell extends TreeCell<SelectableOntologyTreeTerm> {

    private final TermSelectionButton selectionButton;

    private BooleanProperty includedProperty;
    private BooleanProperty excludedProperty;

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
            setText(item.term().getName());
            setGraphic(selectionButton);

            if (includedProperty != null)
                selectionButton.includedProperty().unbindBidirectional(includedProperty);
            if (excludedProperty != null)
                selectionButton.excludedProperty().unbindBidirectional(excludedProperty);

            includedProperty = item.includedProperty();
            selectionButton.includedProperty().bindBidirectional(includedProperty);
            excludedProperty = item.excludedProperty();
            selectionButton.excludedProperty().bindBidirectional(excludedProperty);

        }
    }

}

