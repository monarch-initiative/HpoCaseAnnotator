package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;

public class SexTableCell<T> extends TableCell<T, Sex> {

    public static <T> Callback<TableColumn<T, Sex>, TableCell<T, Sex>> of() {
        return tc -> new SexTableCell<>();
    }

    @Override
    protected void updateItem(Sex item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
        } else {
            setText(null);
        }
    }
}
