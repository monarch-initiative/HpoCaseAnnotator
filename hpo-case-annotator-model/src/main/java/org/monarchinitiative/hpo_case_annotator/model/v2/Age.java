package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Optional;

public interface Age {

    static Age ofYearsMonthsDays(int years, int months, int days) {
        return of( years, months, days);
    }

    static Age of(Integer years, Integer months, Integer days) {
        return new AgeDefault(years, months, days);
    }

    Integer getYears();

    Integer getMonths();

    Integer getDays();

    // ******************************************** DERIVED METHODS ************************************************** *

    default Optional<String> getIso8601Duration() {
        Integer years = getYears();
        Integer months = getMonths();
        Integer days = getDays();

        if (years == null && months == null && days == null)
            return Optional.empty();

        StringBuilder builder = new StringBuilder("P");
        if (years != null && years != 0)
            builder.append("%sY".formatted(years));

        if (months != null && months != 0)
            builder.append("%sM".formatted(months));

        if (days != null && days != 0)
            builder.append("%sD".formatted(days));

        return Optional.of(builder.toString());
    }

}
