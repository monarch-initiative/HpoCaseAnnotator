package org.monarchinitiative.hpo_case_annotator.forms.component;

import javafx.beans.binding.BooleanBinding;
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
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class IndividualIdsComponent extends VBox {
    private static final String DEFAULT_STYLECLASS = "individual-ids-component";
    private final ObjectProperty<ObservableAge> age = new SimpleObjectProperty<>(new ObservableAge());

    @FXML
    private Label idLabel;
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

    public IndividualIdsComponent() {
        getStyleClass().add(DEFAULT_STYLECLASS);
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
        idLabel.textProperty().bind(individualId.textProperty());

        ageSummary.textProperty().bind(ageSummaryBinding());
    }

    private ObservableValue<String> ageSummaryBinding() {
        BooleanBinding isGestational = selectBoolean(age, "gestational");
        ObjectBinding<Integer> years = select(age, "years");
        ObjectBinding<Integer> months = select(age, "months");
        ObjectBinding<Integer> weeks = select(age, "weeks");
        ObjectBinding<Integer> days = select(age, "days");
        return new StringBinding() {
            {
                bind(age, isGestational, years, months, weeks, days);
            }
            @Override
            protected String computeValue() {
                if (age.get() == null)
                    return "Not provided";

                StringBuilder builder = new StringBuilder();
                if (isGestational.get()) {
                    builder.append("Gestational age");
                    Integer w = weeks.getValue();
                    if (w != null)
                        builder.append(", ")
                                .append(w)
                                .append(" weeks");
                } else {
                    builder.append("Postnatal age");
                    Integer y = years.getValue();
                    if (y != null)
                        builder.append(", ")
                                .append(y)
                                .append(" years");
                    Integer m = months.getValue();
                    if (m != null)
                        builder.append(", ")
                                .append(m)
                                .append(" months");
                }
                Integer d = days.getValue();
                if (d != null)
                    builder.append(", ")
                            .append(d)
                            .append(" days");

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
    public ObjectProperty<ObservableAge> ageProperty() {
        return age;
    }
    public StringProperty probandProperty() {
        return proband.textProperty();
    }

}
