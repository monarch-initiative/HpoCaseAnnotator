package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVitalStatus;

import java.io.IOException;

public class VitalStatusBindingComponent extends VBoxBindingObservableDataComponent<ObservableVitalStatus> {

    @FXML
    private TitledComboBox<VitalStatus.Status> vitalStatus;
    @FXML
    private CheckBox timeOfDeathIsUnknown;
    @FXML
    private TimeElementBindingComponent timeOfDeathComponent;

    public VitalStatusBindingComponent() {
        FXMLLoader loader = new FXMLLoader(VitalStatusBindingComponent.class.getResource("VitalStatusBindingComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        super.initialize();
        timeOfDeathComponent.disableProperty().bind(timeOfDeathIsUnknown.selectedProperty()
                .or(vitalStatus.valueProperty().isNotEqualTo(VitalStatus.Status.DECEASED)));
        vitalStatus.getItems().addAll(VitalStatus.Status.values());

        timeOfDeathIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (isUnknown) {
                timeOfDeathComponent.setData(null);
            } else {
                timeOfDeathComponent.setData(new ObservableTimeElement());
            }
        });
    }

    @Override
    protected void unbind(ObservableVitalStatus data) {
        vitalStatus.valueProperty().unbindBidirectional(data.statusProperty());
        timeOfDeathComponent.dataProperty().unbindBidirectional(data.timeOfDeathProperty());
        clear();
    }

    @Override
    protected void bind(ObservableVitalStatus data) {
        vitalStatus.valueProperty().bindBidirectional(data.statusProperty());

        timeOfDeathIsUnknown.setSelected(data.getTimeOfDeath() == null);
        timeOfDeathComponent.dataProperty().bindBidirectional(data.timeOfDeathProperty());
    }

    private void clear() {
        vitalStatus.setValue(VitalStatus.Status.UNKNOWN);
        timeOfDeathIsUnknown.setSelected(true);
        timeOfDeathComponent.setData(null);
    }

}
