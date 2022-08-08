package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVitalStatus;

import java.io.IOException;
import java.util.Objects;

public class VitalStatusEditableComponent extends HBox implements DataEditController<ObservableVitalStatus> {

    private ObservableVitalStatus item;

    private ObservableTimeElement timeOfDeath;
    @FXML
    private TitledComboBox<VitalStatus.Status> vitalStatus;
    @FXML
    private CheckBox timeOfDeathIsUnknown;
    @FXML
    private TimeElementEditableComponent timeOfDeathComponent;

    public VitalStatusEditableComponent() {
        FXMLLoader loader = new FXMLLoader(VitalStatusEditableComponent.class.getResource("VitalStatusEditableComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        vitalStatus.getItems().addAll(VitalStatus.Status.values());
        timeOfDeathComponent.disableProperty().bind(timeOfDeathIsUnknown.selectedProperty());
    }

    @Override
    public void setInitialData(ObservableVitalStatus data) {
        item = Objects.requireNonNull(data);

        vitalStatus.setValue(item.getStatus() == null ? VitalStatus.Status.UNKNOWN : item.getStatus());

        boolean timeOfDeathIsUnknownRightNow = item.getTimeOfDeath() == null;
        timeOfDeathIsUnknown.setSelected(timeOfDeathIsUnknownRightNow);
        timeOfDeath = timeOfDeathIsUnknownRightNow
                ? new ObservableTimeElement()
                : item.getTimeOfDeath();

        timeOfDeathComponent.setInitialData(timeOfDeath);
    }

    @Override
    public void commit() {
        item.setStatus(vitalStatus.getValue());
        if (timeOfDeathIsUnknown.isSelected()) {
            item.setTimeOfDeath(null);
        } else {
            timeOfDeathComponent.commit();
            item.setTimeOfDeath(timeOfDeath);
        }
    }
}
