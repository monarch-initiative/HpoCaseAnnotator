package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TimeElementEditableComponent extends VBox implements DataEditController<ObservableTimeElement> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeElementEditableComponent.class);
    private final ObjectProperty<ObservableTimeElement> data = new SimpleObjectProperty<>();

    @FXML
    private CheckBox ageIsUnknown;
    @FXML
    private VBox box;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab gestationalAgeTab;
    @FXML
    private TitledTextField gestationalWeeks;
    private final TextFormatter<Integer> gestationalWeeksFormatter = Formats.integerFormatter();
    @FXML
    private TitledComboBox<Integer> gestationalDays;

    @FXML
    private Tab ageTab;
    @FXML
    private SimpleEditableAge age;

    @FXML
    private Tab ageRangeTab;
    @FXML
    private SimpleEditableAge ageRangeStart;
    @FXML
    private SimpleEditableAge ageRangeEnd;

    @FXML
    private Tab ontologyClassTab;
    @FXML
    private ComboBox<TermId> ontologyClassComboBox;

    public TimeElementEditableComponent() {
        FXMLLoader loader = new FXMLLoader(TimeElementEditableComponent.class.getResource("TimeElementEditableComponent.fxml"));
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
        box.disableProperty().bind(ageIsUnknown.selectedProperty());

        gestationalWeeks.setTextFormatter(gestationalWeeksFormatter);
        gestationalDays.getItems().addAll(FormUtils.getIntegers(6));

        // TODO - add all HPO onset terms
//        ontologyClassComboBox.getItems().addAll();

        // clear the
        ageIsUnknown.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                data.set(null);
                clearForm();
            } else {
                data.set(new ObservableTimeElement());
            }
        });

        data.addListener((obs, old, novelAge) -> {
            ageIsUnknown.setSelected(novelAge == null);

            if (novelAge != null) {
                TimeElement.TimeElementCase tc = novelAge.getTimeElementCase();
                if (tc != null) {
                    switch (tc) {
                        case GESTATIONAL_AGE -> {
                            tabPane.getSelectionModel().select(gestationalAgeTab);
                            ObservableGestationalAge ge = novelAge.getGestationalAge();
                            gestationalWeeksFormatter.setValue(ge == null ? null : ge.getWeeks());
                            gestationalDays.setValue(ge == null ? null : ge.getDays());
                        }
                        case AGE -> {
                            tabPane.getSelectionModel().select(ageTab);
                            age.setAge(novelAge.getAge());
                        }
                        case AGE_RANGE -> {
                            tabPane.getSelectionModel().select(ageRangeTab);
                            ObservableAgeRange ar = novelAge.getAgeRange();
                            ageRangeStart.setAge(ar == null ? null : ar.getStart());
                            ageRangeEnd.setAge(ar == null ? null : ar.getEnd());
                        }
                        case ONTOLOGY_CLASS -> {
                            tabPane.getSelectionModel().select(ontologyClassTab);
                            ontologyClassComboBox.setValue(novelAge.getOntologyClass());
                        }
                    }
                }
            }
        });

    }

    private void clearForm() {
        gestationalWeeks.setText(null);
        gestationalDays.setValue(null);

        age.clean();

        ageRangeStart.clean();
        ageRangeEnd.clean();

        ontologyClassComboBox.setValue(null);
    }

    @FXML
    private void clearGestationalWeeks(ActionEvent e) {
        gestationalWeeksFormatter.setValue(null);
        e.consume();
    }

    @FXML
    private void clearGestationalDays(ActionEvent e) {
        gestationalDays.setValue(null);
        e.consume();
    }

    @FXML
    private void clearOntologyClass(ActionEvent e) {
        ontologyClassComboBox.setValue(null);
        e.consume();
    }

    @Override
    public void setInitialData(ObservableTimeElement item) {
        data.set(item);
    }

    @Override
    public ObservableTimeElement getEditedData() {
        ObservableTimeElement item = data.get();

        if (item != null && !ageIsUnknown.isSelected()) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab.equals(gestationalAgeTab)) {
                item.setTimeElementCase(TimeElement.TimeElementCase.GESTATIONAL_AGE);

                ObservableGestationalAge oga = new ObservableGestationalAge();
                oga.setWeeks(gestationalWeeksFormatter.getValue());
                oga.setDays(gestationalDays.getValue());
                item.setGestationalAge(oga);
            } else if (selectedTab.equals(ageTab)) {
                item.setTimeElementCase(TimeElement.TimeElementCase.AGE);

                item.setAge(age.getAge());
            } else if (selectedTab.equals(ageRangeTab)) {
                item.setTimeElementCase(TimeElement.TimeElementCase.AGE_RANGE);

                ObservableAgeRange ar = new ObservableAgeRange();
                ar.setStart(ageRangeStart.getAge());
                ar.setEnd(ageRangeEnd.getAge());
                item.setAgeRange(ar);
            } else if (selectedTab.equals(ontologyClassTab)) {
                item.setTimeElementCase(TimeElement.TimeElementCase.ONTOLOGY_CLASS);
                item.setOntologyClass(ontologyClassComboBox.getValue());
            } else {
                LOGGER.warn("Illegal state, unknown tab is selected: {}", selectedTab.getText());
            }
        }

        return item;
    }

    public ObjectProperty<ObservableTimeElement> dataProperty() {
        return data;
    }
}
