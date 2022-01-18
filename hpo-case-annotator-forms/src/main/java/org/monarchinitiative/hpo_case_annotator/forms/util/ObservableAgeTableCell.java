package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableAge;

public class ObservableAgeTableCell<T> extends TableCell<T, ObservableAge> {

    public static <T> Callback<TableColumn<T, ObservableAge>, TableCell<T, ObservableAge>> of() {
        return tc -> new ObservableAgeTableCell<>();
    }

    @Override
    protected void updateItem(ObservableAge item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            Integer years = item.getYears();
            Integer months = item.getMonths();
            Integer days = item.getDays();
            setText(String.format("P%dY%dM%dD", zeroIfNull(years), zeroIfNull(months), zeroIfNull(days)));
        } else {
            setText(null);
        }
    }

    private static int zeroIfNull(Integer years) {
        return years == null ? 0 : years;
    }
}
