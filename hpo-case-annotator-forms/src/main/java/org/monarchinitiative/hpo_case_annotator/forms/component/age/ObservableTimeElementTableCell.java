package org.monarchinitiative.hpo_case_annotator.forms.component.age;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class ObservableTimeElementTableCell<T> extends TableCell<T, ObservableTimeElement> {

    private static final Map<TimeElement.TimeElementCase, Image> ICONS = loadIcons();

    private final ImageView imageView;

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

    public ObservableTimeElementTableCell() {
        setContentDisplay(ContentDisplay.LEFT);
        imageView = new ImageView();
        imageView.setFitHeight(20.);
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }

    @Override
    protected void updateItem(ObservableTimeElement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            imageView.setImage(null);
            setText(null);
        } else {
            imageView.setImage(ICONS.get(item.getTimeElementCase()));
            setText(summarizeTimeElement(item));
        }
    }

    private static String summarizeTimeElement(ObservableTimeElement item) {
        return switch (item.getTimeElementCase()) {
            case GESTATIONAL_AGE -> summarizeGestationalAge(item.getGestationalAge());
            case AGE -> summarizeAge(item.getAge());
            case AGE_RANGE -> summarizeAgeRange(item.getAgeRange());
            case ONTOLOGY_CLASS -> item.getOntologyClass().getValue();
        };
    }

    private static String summarizeGestationalAge(ObservableGestationalAge gestationalAge) {
        StringBuilder builder = new StringBuilder();
        Integer weeks = gestationalAge.getWeeks();
        if (weeks != null) {
            builder.append(weeks);
            if (weeks != 1)
                builder.append(" weeks ");
            else builder.append(" week ");
        }

        Integer days = gestationalAge.getDays();
        if (days != null) {
            builder.append(days);
            if (days != 1)
                builder.append(" days");
            else builder.append(" day");
        }
        return builder.toString();
    }

    private static String summarizeAge(ObservableAge age) {
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
        return "%s - %s".formatted(summarizeAge(ageRange.getStart()), summarizeAge(ageRange.getEnd()));
    }
}
