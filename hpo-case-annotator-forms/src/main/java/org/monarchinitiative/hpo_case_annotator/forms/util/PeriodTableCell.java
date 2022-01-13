package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.Period;

public class PeriodTableCell<T> extends TableCell<T, Period> {

    public static <T> Callback<TableColumn<T, Period>, TableCell<T, Period>> of() {
        return tc -> new PeriodTableCell<>();
    }

    @Override
    protected void updateItem(Period item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
        } else {
            setText(null);
        }
    }
}
