package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableIndividual;

import java.io.IOException;

public class IndividualIdsBindingComponent extends HBox implements ObservableDataController<ObservableIndividual> {

    private final ObjectProperty<ObservableIndividual> data = new SimpleObjectProperty<>();
    @FXML
    private TitledTextField individualId;
    @FXML
    private TitledComboBox<Sex> sex;
    @FXML
    private TimeElementBindingComponent ageComponent;
    @FXML
    private VitalStatusBindingComponent vitalStatusComponent;

    public IndividualIdsBindingComponent() {
        FXMLLoader loader = new FXMLLoader(IndividualIdsBindingComponent.class.getResource("IndividualIdsBindingComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObjectProperty<ObservableIndividual> dataProperty() {
        return data;
    }

    @FXML
    protected void initialize() {
        sex.getItems().addAll(Sex.values());
        dataProperty().addListener(onDataChange());
    }

    private ChangeListener<ObservableIndividual> onDataChange() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

    private void bind(ObservableIndividual data) {
        individualId.textProperty().bindBidirectional(data.idProperty());
        sex.valueProperty().bindBidirectional(data.sexProperty());
        ageComponent.dataProperty().bindBidirectional(data.ageProperty());
        vitalStatusComponent.dataProperty().bindBidirectional(data.vitalStatusProperty());
    }

    private void unbind(ObservableIndividual data) {
        individualId.textProperty().unbindBidirectional(data.idProperty());
        sex.valueProperty().unbindBidirectional(data.sexProperty());
        ageComponent.dataProperty().unbindBidirectional(data.ageProperty());
        vitalStatusComponent.dataProperty().unbindBidirectional(data.vitalStatusProperty());
    }
}
