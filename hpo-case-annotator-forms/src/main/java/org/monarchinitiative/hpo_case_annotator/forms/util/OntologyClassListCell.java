package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;

/**
 * A list cell for presenting {@link OntologyClass}. The label is presented to the user.
 */
public class OntologyClassListCell extends ListCell<OntologyClass> {

    public static Callback<ListView<OntologyClass>, ListCell<OntologyClass>> cellFactory() {
        return lw -> new OntologyClassListCell();
    }

    @Override
    protected void updateItem(OntologyClass item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            setText(item.getLabel());
        } else {
            setText(null);
        }
    }
}
