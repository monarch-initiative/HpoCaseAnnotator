package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.component.age.TimeElementComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class IndividualIdsComponent extends VBox {
    private final ObjectProperty<ObservableTimeElement> age = new SimpleObjectProperty<>();
    @FXML
    private TitledLabel individualId;
    @FXML
    private TitledLabel paternalId;
    @FXML
    private TitledLabel maternalId;
    @FXML
    private TitledLabel sex;
    @FXML
    private TitledLabel proband;
    @FXML
    private Label ageSummary;
    @FXML
    private TitledLabel vitalStatus;
    @FXML
    private TimeElementComponent timeOfDeath;

    public IndividualIdsComponent() {
        FXMLLoader loader = new FXMLLoader(IndividualIdsComponent.class.getResource("IndividualIdsComponent.fxml"));
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
                    case AGE_RANGE ->
                            builder.append("Using age range to represent individual's age is not supported.");
                    case ONTOLOGY_CLASS ->
                            builder.append("Using ontology class to represent individual's age is not supported.");
                }

                return builder.toString();
            }
        };
    }

    public StringProperty individualIdProperty() {
        return individualId.textProperty();
    }

    public StringProperty paternalIdProperty() {
        return paternalId.textProperty();
    }

    public StringProperty maternalIdProperty() {
        return maternalId.textProperty();
    }

    public String getSex() {
        return sex.getText();
    }
    public void setSex(String sex) {
        this.sex.setText(sex);
    }
    public StringProperty sexProperty() {
        return sex.textProperty();
    }
    public ObjectProperty<ObservableTimeElement> ageProperty() {
        return age;
    }
    public StringProperty probandProperty() {
        return proband.textProperty();
    }
    public StringProperty vitalStatusProperty() {
        return vitalStatus.textProperty();
    }
    public ObjectProperty<ObservableTimeElement> timeOfDeathProperty() {
        return timeOfDeath.dataProperty();
    }

}
