package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class TimeElementComponent extends VBox implements ObservableDataController<ObservableTimeElement> {

    private final ObjectProperty<ObservableTimeElement> data = new SimpleObjectProperty<>();

    @FXML
    private VBox box;
    private final GestationalAge gestationalAge;
    private final Age age;
    private final AgeRange ageRange;
    private final OntologyClassAge ontologyClassAge;
    private final Label noAgePlaceholder;

    public TimeElementComponent() {
        gestationalAge = new GestationalAge();
        age = new Age();
        ageRange = new AgeRange();
        ontologyClassAge = new OntologyClassAge();
        noAgePlaceholder = new Label("N/A");
        noAgePlaceholder.setStyle("-fx-text-fill: darkgrey");

        FXMLLoader loader = new FXMLLoader(TimeElementComponent.class.getResource("TimeElementComponent.fxml"));
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
        data.addListener((obs) -> {
            ObservableTimeElement novel = data.get();
            if (novel == null) {
                box.setDisable(true);
                setContent(noAgePlaceholder);
            } else {
                box.setDisable(false);
                switch (novel.getTimeElementCase()) {
                    case GESTATIONAL_AGE -> setContent(gestationalAge);
                    case AGE -> setContent(age);
                    case AGE_RANGE -> setContent(ageRange);
                    case ONTOLOGY_CLASS -> setContent(ontologyClassAge);
                }
            }
        });

        gestationalAge.weeksProperty().bind(selectInteger(data, "gestationalAge", "weeks").asString());
        gestationalAge.daysProperty().bind(selectInteger(data, "gestationalAge", "days").asString());

        age.yearsProperty().bind(selectInteger(data, "age", "years").asString());
        age.monthsProperty().bind(selectInteger(data, "age", "months").asString());
        age.daysProperty().bind(selectInteger(data, "age", "days").asString());

        ageRange.startYearsProperty().bind(selectInteger(data, "ageRange", "start", "years").asString());
        ageRange.startMonthsProperty().bind(selectInteger(data, "ageRange", "start", "months").asString());
        ageRange.startDaysProperty().bind(selectInteger(data, "ageRange", "start", "days").asString());

        ageRange.endYearsProperty().bind(selectInteger(data, "ageRange", "end", "years").asString());
        ageRange.endMonthsProperty().bind(selectInteger(data, "ageRange", "end", "months").asString());
        ageRange.endDaysProperty().bind(selectInteger(data, "ageRange", "end", "days").asString());

        ontologyClassAge.termIdProperty().bind(select(data, "ontologyClass"));
    }

    private void setContent(Node node) {
        box.getChildren().clear();
        box.getChildren().add(node);
    }

    @Override
    public ObjectProperty<ObservableTimeElement> dataProperty() {
        return data;
    }

}
