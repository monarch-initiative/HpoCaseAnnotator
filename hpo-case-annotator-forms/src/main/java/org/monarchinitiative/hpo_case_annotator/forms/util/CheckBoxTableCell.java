package org.monarchinitiative.hpo_case_annotator.forms.util;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class CheckBoxTableCell<T> extends TableCell<T, Boolean> {

    private final CheckBox checkBox;

    public CheckBoxTableCell() {
        checkBox = new CheckBox();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);
    }


    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            // we negate as the item represents "isExcluded" and we need to show "isPresent"
            checkBox.setSelected(!item);
            setGraphic(checkBox);
        }
    }
}
