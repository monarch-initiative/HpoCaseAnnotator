package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementEditableComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public abstract class BaseIndividualIdsEditableComponent<T extends BaseObservableIndividual> extends VBox implements DataEditController<T> {

    protected T item;

    @FXML
    private TitledTextField individualId;
    @FXML
    private TitledComboBox<Sex> sex;
    @FXML
    private TimeElementEditableComponent ageComponent;
    @FXML
    private VitalStatusEditableComponent vitalStatusComponent;

    protected BaseIndividualIdsEditableComponent(URL location) {
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
    }

    @Override
    public void setInitialData(T data) {
        item = Objects.requireNonNull(data);

        individualId.setText(data.getId());
        sex.setValue(data.getSex());
        ageComponent.setInitialData(data.getAge());
        vitalStatusComponent.setInitialData(data.getVitalStatus());
    }

    @Override
    public void commit() {
        item.setId(individualId.getText());
        item.setSex(sex.getValue());
        ageComponent.commit();
        vitalStatusComponent.commit();
    }
}
