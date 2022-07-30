package org.monarchinitiative.hpo_case_annotator.forms.pedigree;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

class TermIdTableCell extends TableCell<ObservablePhenotypicFeature, TermId> {

    TermIdTableCell(TableColumn<ObservablePhenotypicFeature, TermId> column) {

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
