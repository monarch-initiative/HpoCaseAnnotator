package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.scene.control.TreeCell;

class PhenotypeFeatureTreeCell extends TreeCell<PhenotypeFeatureItem> {

    private final SelectionStatusButton selectionButton = new SelectionStatusButton();

    @Override
    protected void updateItem(PhenotypeFeatureItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(selectionButton);
            setText(item.getName());

            selectionButton.setSelectionStatus(item.getSelectionStatus());
            item.selectionStatusProperty().bind(selectionButton.selectionStatusBinding());
        }
    }

}

