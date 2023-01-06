package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TableCell;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class TermIdTableCell<T> extends TableCell<T, TermId> {

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
