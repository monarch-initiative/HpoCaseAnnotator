package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxBindingObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import static javafx.beans.binding.Bindings.select;

public class BaseIndividualIdsComponent<T extends BaseObservableIndividual> extends VBoxBindingObservableDataController<T> {

    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>();

    @FXML
    private TitledLabel individualId;
    @FXML
    private TitledLabel sex;
    @FXML
    private TitledLabel ageSummary;
    @FXML
    private TitledLabel vitalStatus;
    @FXML
    private TimeElementComponent timeOfDeath;

    @FXML
    protected void initialize() {
        super.initialize();
        ageSummary.textProperty().bind(ageSummaryBinding());
    }

    private ObservableValue<String> ageSummaryBinding() {
        ObjectBinding<TimeElement.TimeElementCase> timeElementCase = select(age, "timeElementCase");
        ObjectBinding<Integer> ageYears = select(age, "age", "years");
        ObjectBinding<Integer> ageMonths = select(age, "age", "months");
        ObjectBinding<Integer> ageDays = select(age, "age", "days");

        ObjectBinding<Integer> gestationalWeeks = select(age, "gestationalAge", "weeks");
        ObjectBinding<Integer> gestationalDays = select(age, "gestationalAge", "days");
        return new StringBinding() {
            {
                bind(age, timeElementCase, ageYears, ageMonths, ageDays, gestationalWeeks, gestationalDays);
            }

            @Override
            protected String computeValue() {
                if (age.get() == null)
                    return "Not provided";

                StringBuilder builder = new StringBuilder();
                switch (timeElementCase.getValue()) {
                    case GESTATIONAL_AGE -> {
                        builder.append("Gestational age");
                        Integer gw = gestationalWeeks.getValue();
                        if (gw != null)
                            builder.append(": ")
                                    .append(gw)
                                    .append(" weeks");
                        Integer gd = gestationalDays.getValue();
                        if (gd != null)
                            builder.append(", ")
                                    .append(gd)
                                    .append(" days");
                    }
                    case AGE -> {
                        builder.append("Age");
                        Integer ay = ageYears.getValue();
                        if (ay != null)
                            builder.append(": ")
                                    .append(ay)
                                    .append(" years");
                        Integer am = ageMonths.getValue();
                        if (am != null)
                            builder.append(", ")
                                    .append(am)
                                    .append(" months");
                        Integer ad = ageDays.getValue();
                        if (ad != null)
                            builder.append(", ")
                                    .append(ad)
                                    .append(" days");
                    }
                    case AGE_RANGE -> builder.append("Using age range to represent individual's age is not supported.");
                    case ONTOLOGY_CLASS ->
                            builder.append("Using ontology class to represent individual's age is not supported.");
                }

                return builder.toString();
            }
        };
    }

    @Override
    protected void bind(T data) {
        individualId.textProperty().bind(data.idProperty());
        sex.textProperty().bind(data.sexProperty().asString());
        age.bind(data.ageProperty());
        vitalStatus.textProperty().bind(select(data, "vitalStatus", "status").asString());
        timeOfDeath.dataProperty().bind(select(data, "vitalStatus", "timeOfDeath"));
    }

    @Override
    protected void unbind(T data) {
        individualId.textProperty().unbind();
        sex.textProperty().unbind();
        age.unbind();
        vitalStatus.textProperty().unbind();
        timeOfDeath.dataProperty().unbind();
    }
}
