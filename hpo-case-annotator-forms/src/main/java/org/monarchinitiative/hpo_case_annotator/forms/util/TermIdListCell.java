package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class TermIdListCell extends ListCell<TermId> {

    public static Callback<ListView<TermId>, ListCell<TermId>> of() {
        return lw -> new ListCell<>();
    }

    @Override
    protected void updateItem(TermId item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.getValue());
        }
    }
}
