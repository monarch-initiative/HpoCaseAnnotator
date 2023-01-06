package org.monarchinitiative.hpo_case_annotator.forms.util;

import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAgeRange;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGestationalAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableTimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class TimeElementUtils {

    public static final String NA = "N/A";

    public static String summarizeObservableTimeElement(ObservableTimeElement item) {
        if (item == null)
            return NA;
        return switch (item.getTimeElementCase()) {
            case GESTATIONAL_AGE -> summarizeGestationalAge(item.getGestationalAge());
            case AGE -> summarizeAge(item.getAge());
            case AGE_RANGE -> summarizeAgeRange(item.getAgeRange());
            case ONTOLOGY_CLASS -> summarizeOntologyClass(item.getOntologyClass());
        };
    }

    private static String summarizeGestationalAge(ObservableGestationalAge gestationalAge) {
        if (gestationalAge == null)
            return NA;

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
            return NA;

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
            return NA;

        return "%s - %s".formatted(summarizeAge(ageRange.getStart()), summarizeAge(ageRange.getEnd()));
    }

    private static String summarizeOntologyClass(TermId item) {
        return item == null ? NA : item.getValue();
    }
}
