package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxDataEdit;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class TimeElementDataEdit extends VBoxDataEdit<ObservableTimeElement> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeElementDataEdit.class);
    private ObservableTimeElement data;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab gestationalAgeTab;
    @FXML
    private TitledTextField gestationalWeeks;
    private final TextFormatter<Integer> gestationalWeeksFormatter = TextFormatters.integerFormatter();
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

    public TimeElementDataEdit() {
        FXMLLoader loader = new FXMLLoader(TimeElementDataEdit.class.getResource("TimeElementDataEdit.fxml"));
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
        gestationalWeeks.setTextFormatter(gestationalWeeksFormatter);
        gestationalDays.getItems().addAll(FormUtils.getIntegers(6));

        // TODO - add all HPO onset terms
//        ontologyClassComboBox.getItems().addAll();

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
        data = Objects.requireNonNull(item);

        TimeElement.TimeElementCase tc = item.getTimeElementCase();
        if (tc != null) {
            switch (tc) {
                case GESTATIONAL_AGE -> {
                    tabPane.getSelectionModel().select(gestationalAgeTab);
                    ObservableGestationalAge ge = item.getGestationalAge();
                    gestationalWeeksFormatter.setValue(ge == null ? null : ge.getWeeks());
                    gestationalDays.setValue(ge == null ? null : ge.getDays());
                }
                case AGE -> {
                    tabPane.getSelectionModel().select(ageTab);
                    age.setAge(item.getAge());
                }
                case AGE_RANGE -> {
                    tabPane.getSelectionModel().select(ageRangeTab);
                    ObservableAgeRange ar = item.getAgeRange();
                    ageRangeStart.setAge(ar == null ? null : ar.getStart());
                    ageRangeEnd.setAge(ar == null ? null : ar.getEnd());
                }
                case ONTOLOGY_CLASS -> {
                    tabPane.getSelectionModel().select(ontologyClassTab);
                    ontologyClassComboBox.setValue(item.getOntologyClass());
                }
            }
        }
    }

    @Override
    public void commit() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.equals(gestationalAgeTab)) {
            data.setTimeElementCase(TimeElement.TimeElementCase.GESTATIONAL_AGE);

            ObservableGestationalAge oga = new ObservableGestationalAge();
            oga.setWeeks(gestationalWeeksFormatter.getValue());
            oga.setDays(gestationalDays.getValue());
            data.setGestationalAge(oga);
            data.setAge(null);
            data.setAgeRange(null);
            data.setOntologyClass(null);
        } else if (selectedTab.equals(ageTab)) {
            data.setTimeElementCase(TimeElement.TimeElementCase.AGE);

            data.setGestationalAge(null);
            data.setAge(age.getAge());
            data.setAgeRange(null);
            data.setOntologyClass(null);
        } else if (selectedTab.equals(ageRangeTab)) {
            data.setTimeElementCase(TimeElement.TimeElementCase.AGE_RANGE);

            ObservableAgeRange ar = new ObservableAgeRange();
            ar.setStart(ageRangeStart.getAge());
            ar.setEnd(ageRangeEnd.getAge());

            data.setGestationalAge(null);
            data.setAge(null);
            data.setAgeRange(ar);
            data.setOntologyClass(null);
        } else if (selectedTab.equals(ontologyClassTab)) {
            data.setTimeElementCase(TimeElement.TimeElementCase.ONTOLOGY_CLASS);

            data.setGestationalAge(null);
            data.setAge(null);
            data.setAgeRange(null);
            data.setOntologyClass(ontologyClassComboBox.getValue());
        } else {
            LOGGER.warn("Illegal state, unknown tab is selected: {}", selectedTab.getText());
        }
    }

}
