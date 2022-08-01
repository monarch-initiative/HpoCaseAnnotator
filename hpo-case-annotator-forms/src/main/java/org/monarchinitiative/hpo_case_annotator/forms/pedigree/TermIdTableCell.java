package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.monarchinitiative.phenol.ontology.data.TermId;

class TermIdTableCell<T> extends TableCell<T, TermId> {

    TermIdTableCell(TableColumn<T, TermId> column) {

    }

    @Override
    protected void updateItem(TermId item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            setText(item.getValue());
        } else {
            setText(null);
        }
    }
}
