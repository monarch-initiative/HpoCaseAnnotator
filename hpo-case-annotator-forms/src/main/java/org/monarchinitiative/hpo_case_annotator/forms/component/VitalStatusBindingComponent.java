package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVitalStatus;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class VitalStatusBindingComponent extends VBoxBindingObservableDataComponent<ObservableVitalStatus>
        implements Observable {

    static final Callback<VitalStatusBindingComponent, Stream<Observable>> EXTRACTOR = item -> Stream.of(
            item.vitalStatus.valueProperty(),

            item.timeOfDeathIsUnknown.selectedProperty(),
            item.timeOfDeathComponent);

    @FXML
    private TitledComboBox<VitalStatus.Status> vitalStatus;
    @FXML
    private VBox deathBox;
    @FXML
    private CheckBox timeOfDeathIsUnknown;
    @FXML
    private TimeElementBindingComponent timeOfDeathComponent;
    private boolean valueIsNotBeingSetByUserInteraction;

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
        vitalStatus.getItems().addAll(VitalStatus.Status.values());
        vitalStatus.setValue(VitalStatus.Status.UNKNOWN);
        deathBox.disableProperty().bind(vitalStatus.valueProperty().isNotEqualTo(VitalStatus.Status.DECEASED));
        timeOfDeathComponent.disableProperty().bind(timeOfDeathIsUnknown.selectedProperty());

        timeOfDeathIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (isUnknown) {
                timeOfDeathComponent.setData(null);
            } else {
                timeOfDeathComponent.setData(new ObservableTimeElement());
            }
        });
        addListener(this::updateDataModel);
    }

    public List<OntologyClass> onsetOntologyClasses() {
        return timeOfDeathComponent.onsetOntologyClasses();
    }

    private void updateDataModel(Observable obs) {
        if (valueIsNotBeingSetByUserInteraction)
            return;

        ObservableVitalStatus item = data.get();
        if (item == null) {
            // The user is updating the UI but the data has not been set yet. We MUST set it now!
            item = new ObservableVitalStatus();
            data.set(item);
        }

        if (obs.equals(vitalStatus.valueProperty())) {
            item.setStatus(vitalStatus.getValue());
        }
        // We don't care about the rest
    }

    @Override
    protected void bind(ObservableVitalStatus data) {
        try {
            valueIsNotBeingSetByUserInteraction = true;

            if (data != null) {
                vitalStatus.valueProperty().bindBidirectional(data.statusProperty());

                timeOfDeathIsUnknown.setSelected(data.getTimeOfDeath() == null);
                timeOfDeathComponent.dataProperty().bindBidirectional(data.timeOfDeathProperty());
            } else
                clear();
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    @Override
    protected void unbind(ObservableVitalStatus data) {
        if (data != null) {
            vitalStatus.valueProperty().unbindBidirectional(data.statusProperty());
            timeOfDeathComponent.dataProperty().unbindBidirectional(data.timeOfDeathProperty());
        }
    }

    private void clear() {
        vitalStatus.setValue(VitalStatus.Status.UNKNOWN);
        timeOfDeathIsUnknown.setSelected(true);
        timeOfDeathComponent.setData(null);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
