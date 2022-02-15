package org.monarchinitiative.hpo_case_annotator.app.util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.File;

public class FileListCell extends ListCell<File> {

    public static Callback<ListView<File>, ListCell<File>> of() {
        return lw -> new FileListCell();
    }

    private FileListCell() {
    }

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.getAbsolutePath());
        } else {
            setText(null);
        }
    }
}
