package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ObservableDataComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;

import static javafx.beans.binding.Bindings.*;

public class TimeElementComponent extends VBox implements ObservableDataComponent<ObservableTimeElement> {

    private static Label NO_AGE_PLACEHOLDER;
    private final ObjectProperty<ObservableTimeElement> data = new SimpleObjectProperty<>();

    @FXML
    private VBox box;
    private final GestationalAge gestationalAge;
    private final Age age;
    private final AgeRange ageRange;
    private final OntologyClassAge ontologyClassAge;


    public TimeElementComponent() {
        NO_AGE_PLACEHOLDER = new Label("N/A");
        gestationalAge = new GestationalAge();
        age = new Age();
        ageRange = new AgeRange();
        ontologyClassAge = new OntologyClassAge();

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
                setContent(NO_AGE_PLACEHOLDER);
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

        gestationalAge.weeksProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "gestationalAge", "weeks"));
        gestationalAge.daysProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "gestationalAge", "days"));

        age.yearsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "age", "years"));
        age.monthsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "age", "months"));
        age.daysProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "age", "days"));

        ageRange.startYearsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "start", "years"));
        ageRange.startMonthsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "start", "months"));
        ageRange.startDaysProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "start", "days"));

        ageRange.endYearsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "end", "years"));
        ageRange.endMonthsProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "end", "months"));
        ageRange.endDaysProperty().bind(toIntegerOrShowPlaceholderIfNull(data, "ageRange", "end", "days"));

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

    private static StringBinding toIntegerOrShowPlaceholderIfNull(ObjectProperty<ObservableTimeElement> property, String... path) {
        return when(select(property, path).isNull())
                .then("N/A")
                .otherwise(select(property, path).asString());
    }

}
