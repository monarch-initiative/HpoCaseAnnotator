package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;

public class ObservableDiseaseStatusListCell extends ListCell<ObservableDiseaseStatus> {

    public static Callback<ListView<ObservableDiseaseStatus>, ListCell<ObservableDiseaseStatus>> of() {
        return lv -> new ObservableDiseaseStatusListCell();
    }

    @Override
    protected void updateItem(ObservableDiseaseStatus item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            String presence = !item.isExcluded()
                    ? FormUtils.checkMark()
                    : FormUtils.crossMark();
            setText(String.format("%s | %s | %s", presence, item.getDiseaseId(), item.getDiseaseName()));
        }
    }
}
