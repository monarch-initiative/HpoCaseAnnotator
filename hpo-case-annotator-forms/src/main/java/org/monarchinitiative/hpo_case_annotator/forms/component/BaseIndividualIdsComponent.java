package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.monarchinitiative.hpo_case_annotator.forms.base.VBoxBindingObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.net.URL;

import static javafx.beans.binding.Bindings.*;

public class BaseIndividualIdsComponent<T extends BaseObservableIndividual> extends VBoxBindingObservableDataComponent<T> {

    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>();

    @FXML
    private TitledLabel individualId;
    @FXML
    private TitledLabel sex;
    @FXML
    private Label ageSummary;
    @FXML
    private Label vitalStatus;
    @FXML
    private TitledTimeElementComponent timeOfDeath;

    protected BaseIndividualIdsComponent(URL location) {
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
                                    .append(" w");
                        Integer gd = gestationalDays.getValue();
                        if (gd != null)
                            builder.append(" ")
                                    .append(gd)
                                    .append(" d");
                    }
                    case AGE -> {
                        builder.append("Age");
                        Integer ay = ageYears.getValue();
                        if (ay != null)
                            builder.append(": ")
                                    .append(ay)
                                    .append("y");
                        Integer am = ageMonths.getValue();
                        if (am != null)
                            builder.append(" ")
                                    .append(am)
                                    .append("m");
                        Integer ad = ageDays.getValue();
                        if (ad != null)
                            builder.append(" ")
                                    .append(ad)
                                    .append("d");
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
        if (data != null) {
            individualId.textProperty().bind(data.idProperty());
            ObjectProperty<Sex> sexProperty = data.sexProperty();
            sex.textProperty().bind(
                    when(sexProperty.isNotNull())
                            .then(sexProperty.asString())
                            .otherwise("N/A"));
            age.bind(data.ageProperty());
            ObjectBinding<VitalStatus> vitalStatus = select(data, "vitalStatus", "status");
            this.vitalStatus.textProperty().bind(
                    when(vitalStatus.isNotNull())
                            .then(vitalStatus.asString())
                            .otherwise("N/A"));
            timeOfDeath.item.dataProperty().bind(select(data, "vitalStatus", "timeOfDeath"));
        }
    }

    @Override
    protected void unbind(T data) {
        if (data != null) {
            individualId.textProperty().unbind();
            sex.textProperty().unbind();
            age.unbind();
            vitalStatus.textProperty().unbind();
            timeOfDeath.item.dataProperty().unbind();
        }
    }
}
