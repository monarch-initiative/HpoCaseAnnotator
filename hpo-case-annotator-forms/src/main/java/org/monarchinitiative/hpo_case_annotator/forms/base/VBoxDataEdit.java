package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEdit;

/**
 * A {@link DataEdit} implementation that also happens to be a {@link VBox}.
 * @param <T> data type.
 */
public abstract class VBoxDataEdit<T> extends VBox implements DataEdit<T> {

    @FXML
    protected abstract void initialize();
}
