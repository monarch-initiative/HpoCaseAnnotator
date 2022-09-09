package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import org.monarchinitiative.hpo_case_annotator.forms.base.HBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.net.URL;

public abstract class BaseIndividualIdsBindingComponent<T extends BaseObservableIndividual> extends HBoxBindingObservableDataComponent<T> {

    @FXML
    private TitledTextField individualId;
    @FXML
    private TitledComboBox<Sex> sex;

    @FXML
    private CheckBox ageIsUnknown;
    @FXML
    private TimeElementBindingComponent ageComponent;
    @FXML
    private VitalStatusBindingComponent vitalStatusComponent;

    protected boolean valueIsBeingSetProgrammatically;

    protected BaseIndividualIdsBindingComponent(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
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
        sex.getItems().addAll(Sex.values());
        sex.setValue(Sex.UNKNOWN);
        ageComponent.disableProperty().bind(ageIsUnknown.selectedProperty());
        ageIsUnknown.selectedProperty().addListener((obs, old, ageIsUnknown) -> {
            if (valueIsBeingSetProgrammatically)
                return;
            if (ageIsUnknown)
                data.get().setAge(null);
            else
                data.get().setAge(new ObservableTimeElement());
        });
    }

    @Override
    protected void bind(T data) {
        if (data != null) {
            individualId.textProperty().bindBidirectional(data.idProperty());
            sex.valueProperty().bindBidirectional(data.sexProperty());
            ageIsUnknown.setSelected(data.getAge() == null);
            ageComponent.dataProperty().bindBidirectional(data.ageProperty());
            vitalStatusComponent.dataProperty().bindBidirectional(data.vitalStatusProperty());
        } else {
            clear();
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null) {
            individualId.textProperty().unbindBidirectional(data.idProperty());
            sex.valueProperty().unbindBidirectional(data.sexProperty());
            ageComponent.dataProperty().unbindBidirectional(data.ageProperty());
            vitalStatusComponent.dataProperty().unbindBidirectional(data.vitalStatusProperty());
        }
    }

    private void clear() {
        individualId.setText(null);
        sex.setValue(Sex.UNKNOWN);
        ageIsUnknown.setSelected(false);
        ageComponent.setData(null);
        vitalStatusComponent.setData(null);
    }
}
