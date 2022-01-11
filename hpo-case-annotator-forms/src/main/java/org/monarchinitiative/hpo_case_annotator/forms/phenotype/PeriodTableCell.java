package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;

import java.time.Period;

public class PeriodTableCell extends TableCell<ObservablePhenotypicFeature, Period> {

    public static Callback<TableColumn<ObservablePhenotypicFeature, Period>, TableCell<ObservablePhenotypicFeature, Period>> of() {
        return tc -> new PeriodTableCell();
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
