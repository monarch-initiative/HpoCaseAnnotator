package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVitalStatus;

import java.io.IOException;

public class VitalStatusBindingComponent extends VBox implements ObservableDataController<ObservableVitalStatus> {

    private final ObjectProperty<ObservableVitalStatus> data = new SimpleObjectProperty<>();

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
    private void initialize() {
        timeOfDeathComponent.disableProperty().bind(timeOfDeathIsUnknown.selectedProperty());
        vitalStatus.getItems().addAll(VitalStatus.Status.values());
        dataProperty().addListener(onDataChange());

        timeOfDeathIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (isUnknown) {
                timeOfDeathComponent.setData(null);
            } else {
                timeOfDeathComponent.setData(ObservableTimeElement.defaultInstance());
            }
        });
    }

    @Override
    public ObjectProperty<ObservableVitalStatus> dataProperty() {
        return data;
    }

    private ChangeListener<ObservableVitalStatus> onDataChange() {
        return (obs, old, novel) -> {
            if (old != null)
                unbind(old);

            if (novel == null)
                clear();
            else
                bind(novel);
        };
    }

    private void unbind(ObservableVitalStatus data) {
        vitalStatus.valueProperty().unbindBidirectional(data.statusProperty());
        timeOfDeathComponent.dataProperty().unbindBidirectional(data.timeOfDeathProperty());
        clear();
    }

    private void clear() {
        vitalStatus.setValue(VitalStatus.Status.UNKNOWN);
        timeOfDeathIsUnknown.setSelected(true);
        timeOfDeathComponent.setData(null);
    }

    private void bind(ObservableVitalStatus data) {
        vitalStatus.valueProperty().bindBidirectional(data.statusProperty());

        timeOfDeathIsUnknown.setSelected(data.getTimeOfDeath() == null);
        timeOfDeathComponent.dataProperty().bindBidirectional(data.timeOfDeathProperty());
    }

}
