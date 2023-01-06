package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextFormatter;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledComboBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledTextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.stream.Stream;

public class TimeElementBindingComponent
        extends VBoxBindingObservableDataComponent<ObservableTimeElement>
        implements Observable {

    static final Callback<TimeElementBindingComponent, Stream<Observable>> EXTRACTOR = tbc -> Stream.of(
            tbc.tabPane.getSelectionModel().selectedItemProperty(),

            tbc.gestationalWeeks.textProperty(),
            tbc.gestationalDays.valueProperty(),

            tbc.age,

            tbc.ageRangeStart,
            tbc.ageRangeEnd,

            tbc.ontologyClassComboBox.valueProperty());

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

    private boolean valueIsNotBeingSetByUserInteraction;

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

    @FXML
    protected void initialize() {
        super.initialize();
        gestationalDays.getItems().addAll(FormUtils.getIntegers(6));
        gestationalWeeks.setTextFormatter(gestationalWeeksFormatter);

        // Update the data model upon change of each UI component
        addListener(this::updateDataModel);
    }


    private void updateDataModel(Observable obs) {
        if (valueIsNotBeingSetByUserInteraction)
            return;
        ObservableTimeElement te = data.get();
        if (te == null)
            return;

        TimeElement.TimeElementCase tec = mapToTimeElementCase(tabPane.getSelectionModel().getSelectedItem());
        te.setTimeElementCase(tec);
        switch (tec) {
            case GESTATIONAL_AGE -> {
                if (te.getGestationalAge() == null)
                    te.setGestationalAge(new ObservableGestationalAge());
                ObservableGestationalAge ga = te.getGestationalAge();
                ga.setWeeks(gestationalWeeksFormatter.getValue());
                ga.setDays(gestationalDays.getValue());
            }
            case AGE -> {
                if (te.getAge() == null)
                    te.setAge(new ObservableAge());
                copyAge(age, te.getAge());
            }
            case AGE_RANGE -> {
                if (te.getAgeRange() == null)
                    te.setAgeRange(new ObservableAgeRange());

                ObservableAgeRange targetAgeRange = te.getAgeRange();
                if (targetAgeRange.getStart() == null)
                    targetAgeRange.setStart(new ObservableAge());
                copyAge(ageRangeStart, targetAgeRange.getStart());

                if (targetAgeRange.getEnd() == null)
                    targetAgeRange.setEnd(new ObservableAge());
                copyAge(ageRangeEnd, targetAgeRange.getEnd());
            }
            case ONTOLOGY_CLASS -> te.setOntologyClass(ontologyClassComboBox.getValue());
        }
    }

    private static void copyAge(SimpleBindingAge source, ObservableAge target) {
        target.setYears(source.getYears());
        target.setMonths(source.getMonths());
        target.setDays(source.getDays());
    }

    @Override
    protected void bind(ObservableTimeElement data) {
        if (data == null) { // clear
            gestationalWeeksFormatter.setValue(null);
            gestationalDays.setValue(null);
            age.setData(null);
            ageRangeStart.setData(null);
            ageRangeEnd.setData(null);
            ontologyClassComboBox.setValue(null);
        } else {
            TimeElement.TimeElementCase tec = data.getTimeElementCase();
            if (tec == null)
                throw new RuntimeException("Time element case must not be null");

            try {
                valueIsNotBeingSetByUserInteraction = true;
                tabPane.getSelectionModel().select(mapToTimeTab(tec));

                // Gestational age
                ObservableGestationalAge ga = data.getGestationalAge();
                if (ga != null) {
                    gestationalWeeksFormatter.setValue(ga.getWeeks());
                    gestationalDays.setValue(ga.getDays());
                }
                // Age
                age.setData(data.getAge());

                // Age range
                ObservableAgeRange sourceAgeRange = data.getAgeRange();
                if (sourceAgeRange == null) {
                    ageRangeStart.setData(null);
                    ageRangeEnd.setData(null);
                } else {
                    ageRangeStart.setData(sourceAgeRange.getStart());
                    ageRangeEnd.setData(sourceAgeRange.getEnd());
                }

                // Ontology class
                ontologyClassComboBox.setValue(data.getOntologyClass());
            } finally {
                valueIsNotBeingSetByUserInteraction = false;
            }
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

    @Override
    protected void unbind(ObservableTimeElement data) {
        // Not required, no binding is created in the `bind` method
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
        EXTRACTOR.call(this).forEach(obs -> obs.addListener(listener));
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        EXTRACTOR.call(this).forEach(obs -> obs.removeListener(listener));
    }
}
