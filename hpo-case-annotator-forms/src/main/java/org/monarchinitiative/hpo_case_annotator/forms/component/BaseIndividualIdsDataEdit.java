package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementDataEdit;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public abstract class BaseIndividualIdsDataEdit<T extends BaseObservableIndividual> extends VBoxDataEdit<T> {

    private final ObservableList<OntologyClass> onsetOntologyClasses = FXCollections.observableArrayList();

    protected T item;
    private boolean originalAgeWasNull;
    private ObservableTimeElement age;

    @FXML
    private TitledTextField individualId;
    @FXML
    private TitledComboBox<Sex> sex;
    @FXML
    private CheckBox ageIsUnknown;
    @FXML
    private TimeElementDataEdit ageComponent;
    @FXML
    private VitalStatusDataEdit vitalStatusComponent;

    private boolean valueIsNotBeingSetByUserInteraction;

    protected BaseIndividualIdsDataEdit(URL location) {
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
        sex.getItems().addAll(Sex.values());
        ageComponent.visibleProperty().bind(ageIsUnknown.selectedProperty().not());
        ageIsUnknown.selectedProperty().addListener((obs, old, isUnknown) -> {
            if (valueIsNotBeingSetByUserInteraction)
                return;

            if (!isUnknown) {
                if (originalAgeWasNull)
                    ageComponent.setInitialData(age);
                else
                    ageComponent.setInitialData(item.getAge());
            }
        });

        Bindings.bindContent(ageComponent.onsetOntologyClasses(), onsetOntologyClasses);
        Bindings.bindContent(vitalStatusComponent.onsetOntologyClasses(), onsetOntologyClasses);
    }

    public ObservableList<OntologyClass> onsetOntologyClasses() {
        return onsetOntologyClasses;
    }

    @Override
    public void setInitialData(T data) {
        item = Objects.requireNonNull(data);

        originalAgeWasNull = data.getAge() == null;
        age = originalAgeWasNull
                ? new ObservableTimeElement()
                : data.getAge();

        try {
            valueIsNotBeingSetByUserInteraction = true;
            individualId.setText(data.getId());
            sex.setValue(data.getSex());
            ageIsUnknown.setSelected(data.getAge() == null);
            if (data.getAge() != null)
                ageComponent.setInitialData(data.getAge());

            vitalStatusComponent.setInitialData(data.getVitalStatus());
        } finally {
            valueIsNotBeingSetByUserInteraction = false;
        }
    }

    @Override
    public void commit() {
        item.setId(individualId.getText());
        item.setSex(sex.getValue());
        if (ageIsUnknown.isSelected()) {
            item.setAge(null);
        } else {
            if (originalAgeWasNull)
                item.setAge(age);

            ageComponent.commit();
        }

        vitalStatusComponent.commit();
    }
}
