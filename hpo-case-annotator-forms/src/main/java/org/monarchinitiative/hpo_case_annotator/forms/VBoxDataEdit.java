package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * A {@link DataEdit} implementation that also happens to be a {@link VBox}.
 * @param <T> data type.
 */
public abstract class VBoxDataEdit<T> extends VBox implements DataEdit<T> {

    @FXML
    protected abstract void initialize();
}
