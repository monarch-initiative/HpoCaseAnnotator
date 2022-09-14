package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

public class TextMiningController {

    private final ObjectProperty<ObservableTimeElement> encounterTime = new SimpleObjectProperty<>();

    @FXML
    private TabPane contentTabPane;
    @FXML
    private Tab submitTab;
    @FXML
    private Tab reviewTab;
    @FXML
    private TextField textField;

    @FXML
    private void initialize() {

    }

    public ObservableTimeElement getEncounterTime() {
        return encounterTime.get();
    }

    public ObjectProperty<ObservableTimeElement> encounterTimeProperty() {
        return encounterTime;
    }

    public void setEncounterTime(ObservableTimeElement encounterTime) {
        this.encounterTime.set(encounterTime);
    }
}
