package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static javafx.beans.binding.Bindings.select;


// TODO - move to util
public class ObservableTimeElementTableCell<T> extends TableCell<T, ObservableTimeElement> {

    private static final Map<TimeElement.TimeElementCase, Image> ICONS = loadIcons();

    private final ImageView imageView;

    public ObservableTimeElementTableCell() {
        setContentDisplay(ContentDisplay.LEFT);
        imageView = new ImageView();
        imageView.setFitHeight(20.);
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }

    private static Map<TimeElement.TimeElementCase, Image> loadIcons() {
        try {
            return Map.of(
                    TimeElement.TimeElementCase.GESTATIONAL_AGE, loadImage("pregnant.png"),
                    TimeElement.TimeElementCase.AGE, loadImage("person.png"),
                    TimeElement.TimeElementCase.AGE_RANGE, loadImage("person.png"),
                    TimeElement.TimeElementCase.ONTOLOGY_CLASS, loadImage("hpo-logo-black.png")
            );
        } catch (IOException e) {

            return Map.of();
        }

    }

    private static Image loadImage(String name) throws IOException {
        try (InputStream is = ObservableTimeElementTableCell.class.getResourceAsStream(name)) {
            return new Image(is);
        }
    }

    @Override
    protected void updateItem(ObservableTimeElement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            imageView.setImage(null);

            textProperty().unbind();
            setText(null);
        } else {
            item.timeElementCaseProperty().addListener((obs, old, novel) -> imageView.setImage(ICONS.get(novel)));
            // TODO - from some reason the summary is only updated after toggling the tabs.
            textProperty().bind(summarizeTimeElement(item));
        }
    }

    private static StringBinding summarizeTimeElement(ObservableTimeElement item) {
        ObjectBinding<TimeElement.TimeElementCase> timeElementCase = select(item, "timeElementCase");

        ObjectBinding<GestationalAge> gestatioanlAge = select(item, "gestationalAge");
        ObjectBinding<Integer> gestationalWeeks = select(item, "gestationalAge", "weeks");
        ObjectBinding<Integer> gestationalDays = select(item, "gestationalAge", "days");

        ObjectBinding<ObservableAge> age = select(item, "age");
        ObjectBinding<ObservableAge> ageYears = select(item, "age", "years");
        ObjectBinding<ObservableAge> ageMonths = select(item, "age", "months");
        ObjectBinding<ObservableAge> ageDays = select(item, "age", "days");
        ObjectBinding<ObservableAge> start = select(item, "ageRange", "start");
        ObjectBinding<ObservableAge> end = select(item, "ageRange", "end");
        ObjectBinding<TermId> ontologyClass = select(item, "ontologyClass");

        return new StringBinding() {
            {
                // TODO - check if this is working or not. Maybe we don't need to listen to all of this.
                bind(item, timeElementCase,
                        gestatioanlAge, gestationalWeeks, gestationalDays,
                        age, ageYears, ageMonths, ageDays,
                        start, end,
                        ontologyClass);
            }

            @Override
            protected String computeValue() {
                return switch (item.getTimeElementCase()) {
                    case GESTATIONAL_AGE -> summarizeGestationalAge(item.getGestationalAge());
                    case AGE -> summarizeAge(item.getAge());
                    case AGE_RANGE -> summarizeAgeRange(item.getAgeRange());
                    case ONTOLOGY_CLASS -> summarizeOntologyClass(item.getOntologyClass());
                };
            }
        };
    }

    private static String summarizeGestationalAge(ObservableGestationalAge gestationalAge) {
        if (gestationalAge == null)
            return "";

        StringBuilder builder = new StringBuilder();
        Integer weeks = gestationalAge.getWeeks();
        if (weeks != null) {
            builder.append(weeks)
                    .append("w ");
        }

        Integer days = gestationalAge.getDays();
        if (days != null) {
            builder.append(days)
                    .append("d");
        }
        return builder.toString();
    }

    private static String summarizeAge(ObservableAge age) {
        if (age == null)
            return "";

        StringBuilder builder = new StringBuilder();
        Integer years = age.getYears();
        if (years != null) {
            builder.append(years).append("y ");
        }

        Integer months = age.getMonths();
        if (months != null)
            builder.append(months).append("m ");

        Integer days = age.getDays();
        if (days != null)
            builder.append(days).append("d");

        return builder.toString();
    }

    private static String summarizeAgeRange(ObservableAgeRange ageRange) {
        if (ageRange == null)
            return "";

        return "%s - %s".formatted(summarizeAge(ageRange.getStart()), summarizeAge(ageRange.getEnd()));
    }

    private static String summarizeOntologyClass(TermId item) {
        return item == null ? "N/A" : item.getValue();
    }
}
