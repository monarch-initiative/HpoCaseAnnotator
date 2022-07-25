package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.function.Function;

public interface Age {

    Age ZERO = Age.of(false, 0, 0, 0, 0);

    static Age ofYearsMonthsDays(int years, int months, int days) {
        return of(false, years, months, 0, days);
    }

    static Age of(boolean isGestational, int years, int months, int weeks, int days) {
        return new AgeDefault(isGestational, years, months, weeks, days);
    }

    static <T extends Age> AgeBuilder<T> builder(Function<AgeBuilder<T>, T> buildFunction) {
        return new AgeBuilder<>() {
            @Override
            public T build() {
                return buildFunction.apply(this);
            }
        };
    }

    boolean isGestational();

    int getYears();

    int getMonths();

    int getWeeks();

    int getDays();

    // ******************************************** DERIVED METHODS ************************************************** *

    default boolean isPostnatal() {
        return !isGestational();
    }

}
