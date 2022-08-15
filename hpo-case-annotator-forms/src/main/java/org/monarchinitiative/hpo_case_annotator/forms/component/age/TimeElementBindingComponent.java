package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class TimeElementBindingComponent extends VBox implements ObservableDataController<ObservableTimeElement>, Observable {

    static final Callback<TimeElementBindingComponent, Observable[]> EXTRACTOR = tbc -> new Observable[]{
            tbc.tabPane.getSelectionModel().selectedItemProperty(),

            tbc.gestationalWeeks.textProperty(),
            tbc.gestationalDays.valueProperty(),

            tbc.age,
            tbc.ageRangeStart,
            tbc.ageRangeEnd,
            tbc.ontologyClassComboBox.valueProperty()
    };

    private final ObjectProperty<ObservableTimeElement> data = new SimpleObjectProperty<>();

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
    private SimpleBindingAge age;

    @FXML
    private Tab ageRangeTab;
    @FXML
    private SimpleBindingAge ageRangeStart;
    @FXML
    private SimpleBindingAge ageRangeEnd;

    @FXML
    private Tab ontologyClassTab;
    @FXML
    private ComboBox<TermId> ontologyClassComboBox;
    private ObjectBinding<TimeElement.TimeElementCase> tecBinding;

    public TimeElementBindingComponent() {
        FXMLLoader loader = new FXMLLoader(TimeElementBindingComponent.class.getResource("TimeElementBindingComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObjectProperty<ObservableTimeElement> dataProperty() {
        return data;
    }

    @FXML
    private void initialize() {
        gestationalDays.getItems().addAll(FormUtils.getIntegers(6));
        gestationalWeeks.setTextFormatter(gestationalWeeksFormatter);
        dataProperty().addListener((obs, old, novel) -> {
            unbind(old);
            bind(novel);
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener(setupTimeElementCaseTabListener());
        tecBinding = Bindings.createObjectBinding(
                () -> mapToTimeElementCase(tabPane.getSelectionModel().getSelectedItem()),
                tabPane.getSelectionModel().selectedItemProperty());
    }

    private ChangeListener<Tab> setupTimeElementCaseTabListener() {
        return (obs, old, novel) -> {
            ObservableTimeElement ote = data.get();
            if (ote == null)
                return;

            // bind novel
            TimeElement.TimeElementCase novelTec = mapToTimeElementCase(novel);
            switch (novelTec) {
                case GESTATIONAL_AGE -> {
                    if (ote.getGestationalAge() == null)
                        ote.setGestationalAge(ObservableGestationalAge.defaultInstance());
                    gestationalWeeksFormatter.valueProperty().bindBidirectional(ote.getGestationalAge().weeksProperty());
                    gestationalDays.valueProperty().bindBidirectional(ote.getGestationalAge().daysProperty());
                }
                case AGE -> {
                    if (ote.getAge() == null)
                        ote.setAge(ObservableAge.defaultInstance());
                    age.dataProperty().bindBidirectional(ote.ageProperty());
                }
                case AGE_RANGE -> {
                    if (ote.getAgeRange() == null)
                        ote.setAgeRange(ObservableAgeRange.defaultInstance());
                    ObservableAgeRange ar = ote.getAgeRange();
                    if (ar.getStart() == null)
                        ar.setStart(ObservableAge.defaultInstance());
                    if (ar.getEnd() == null)
                        ar.setEnd(ObservableAge.defaultInstance());
                    ageRangeStart.dataProperty().bindBidirectional(ar.startProperty());
                    ageRangeEnd.dataProperty().bindBidirectional(ar.endProperty());
                }
                case ONTOLOGY_CLASS -> ontologyClassComboBox.valueProperty().bindBidirectional(ote.ontologyClassProperty());
            }
        };
    }

    private void bind(ObservableTimeElement data) {
        if (data == null) { // clear
            gestationalWeeksFormatter.setValue(null);
            gestationalDays.setValue(null);
            age.setData(null);
            ageRangeStart.setData(null);
            ageRangeEnd.setData(null);
            ontologyClassComboBox.setValue(null);
        } else {
            TimeElement.TimeElementCase timeElementCase = data.getTimeElementCase();
            if (timeElementCase == null)
                throw new RuntimeException("Time element case must not be null");

            tabPane.getSelectionModel().select(mapToTimeTab(timeElementCase));
            data.timeElementCaseProperty().bind(tecBinding);
        }
    }

    private TimeElement.TimeElementCase mapToTimeElementCase(Tab tab) {
        if (tab.equals(gestationalAgeTab)) {
            return TimeElement.TimeElementCase.GESTATIONAL_AGE;
        } else if (tab.equals(ageTab)) {
            return TimeElement.TimeElementCase.AGE;
        } else if (tab.equals(ageRangeTab)) {
            return TimeElement.TimeElementCase.AGE_RANGE;
        } else if (tab.equals(ontologyClassTab)) {
            return TimeElement.TimeElementCase.ONTOLOGY_CLASS;
        } else {
            throw new RuntimeException("Unexpected tab: %s".formatted(tab));
        }
    }

    private Tab mapToTimeTab(TimeElement.TimeElementCase tec) {
        return switch (tec) {
            case GESTATIONAL_AGE -> gestationalAgeTab;
            case AGE -> ageTab;
            case AGE_RANGE -> ageRangeTab;
            case ONTOLOGY_CLASS -> ontologyClassTab;
        };
    }

    private void unbind(ObservableTimeElement data) {
        if (data != null) {
            data.timeElementCaseProperty().unbind();
        }
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
    public void addListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this))
            observable.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this))
            observable.removeListener(listener);
    }
}
