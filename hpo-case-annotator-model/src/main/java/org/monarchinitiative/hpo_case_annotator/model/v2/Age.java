package org.monarchinitiative.hpo_case_annotator.model.v2;

public interface Age {

    Age ZERO = Age.of(0, 0, 0);

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

}
