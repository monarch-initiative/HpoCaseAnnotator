package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.scene.control.TreeCell;

class SimpleOntologyTreeCell extends TreeCell<SimpleOntologyTreeTerm> {

    static SimpleOntologyTreeCell of() {
        return new SimpleOntologyTreeCell();
    }

    private SimpleOntologyTreeCell() {}

    @Override
    protected void updateItem(SimpleOntologyTreeTerm item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
        } else {
            setText(getItem().term().getName());
        }
    }
}
