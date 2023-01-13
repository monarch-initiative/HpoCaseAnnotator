package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementDataEdit;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVitalStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VitalStatusDataEdit extends HBox implements DataEdit<ObservableVitalStatus> {

    private ObservableVitalStatus item;

    private ObservableTimeElement timeOfDeath;
    @FXML
    private TitledComboBox<VitalStatus.Status> vitalStatus;
    @FXML
    private VBox deathBox;
    @FXML
    private CheckBox timeOfDeathIsUnknown;
    @FXML
    private TimeElementDataEdit timeOfDeathComponent;

    public VitalStatusDataEdit() {
        FXMLLoader loader = new FXMLLoader(VitalStatusDataEdit.class.getResource("VitalStatusDataEdit.fxml"));
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
        BooleanBinding isNotDeceased = vitalStatus.valueProperty().isNotEqualTo(VitalStatus.Status.DECEASED);
        vitalStatus.setValue(VitalStatus.Status.UNKNOWN); // default
        deathBox.disableProperty().bind(isNotDeceased);
        timeOfDeathComponent.disableProperty().bind(timeOfDeathIsUnknown.selectedProperty());
    }

    public List<OntologyClass> onsetOntologyClasses() {
        return timeOfDeathComponent.onsetOntologyClasses();
    }

    @Override
    public void setInitialData(ObservableVitalStatus data) {
        item = Objects.requireNonNull(data);

        vitalStatus.setValue(item.getStatus() == null ? VitalStatus.Status.UNKNOWN : item.getStatus());

        boolean timeOfDeathIsUnknownRightNow = item.getStatus() == null
                || !item.getStatus().equals(VitalStatus.Status.DECEASED)
                || item.getTimeOfDeath() == null;
        timeOfDeathIsUnknown.setSelected(timeOfDeathIsUnknownRightNow);
        timeOfDeath = timeOfDeathIsUnknownRightNow
                ? new ObservableTimeElement()
                : item.getTimeOfDeath();

        timeOfDeathComponent.setInitialData(timeOfDeath);
    }

    @Override
    public void commit() {
        VitalStatus.Status status = vitalStatus.getValue();
        item.setStatus(status);
        if (status.equals(VitalStatus.Status.UNKNOWN) || timeOfDeathIsUnknown.isSelected()) {
            item.setTimeOfDeath(null);
        } else {
            timeOfDeathComponent.commit();
            item.setTimeOfDeath(timeOfDeath);
        }
    }
}
