package org.monarchinitiative.hpo_case_annotator.forms.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;

public abstract class FlowPaneObservableDataComponent<T> extends FlowPane implements ObservableDataComponent<T> {

    protected final ObjectProperty<T> data = new SimpleObjectProperty<>();

    @Override
    public ObjectProperty<T> dataProperty() {
        return data;
    }

    @FXML
    protected abstract void initialize();

}
