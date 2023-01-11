package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementBindingComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;

public abstract class BaseIndividualIdsBindingComponent<T extends BaseObservableIndividual>
        extends VBoxBindingObservableDataComponent<T>
        implements Observable {

    static final  Callback<BaseIndividualIdsBindingComponent<?>, Stream<Observable>> EXTRACTOR = item -> Stream.of(
            item.individualId.textProperty(),
            item.sex.valueProperty(),
            item.ageIsUnknown.selectedProperty(),
            item.ageComponent,
            item.vitalStatusComponent
    );

    private final ObservableList<OntologyClass> onsetOntologyClasses = FXCollections.observableArrayList();

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

    protected boolean valueIsNotBeingSetByUserInteraction;

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
        Bindings.bindContent(ageComponent.onsetOntologyClasses(), onsetOntologyClasses);
        Bindings.bindContent(vitalStatusComponent.onsetOntologyClasses(), onsetOntologyClasses);

        addListener(this::updateDataModel);
    }

    public ObservableList<OntologyClass> onsetOntologyClasses() {
        return onsetOntologyClasses;
    }

    protected void updateDataModel(Observable obs) {
        if (valueIsNotBeingSetByUserInteraction)
            return;
        T item = data.get();
        if (item == null)
            return;

        if (obs.equals(individualId.textProperty())) {
            item.setId(individualId.getText());
        } else if (obs.equals(sex.valueProperty())) {
            item.setSex(sex.getValue());
        } else if (obs.equals(ageIsUnknown.selectedProperty())) {
            if (ageIsUnknown.isSelected())
                item.setAge(null);
            else
                item.setAge(new ObservableTimeElement());
        }
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

    protected abstract Stream<Observable> dependencies();

    @Override
    public void addListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        dependencies().forEach(obs -> obs.removeListener(listener));
    }
}
