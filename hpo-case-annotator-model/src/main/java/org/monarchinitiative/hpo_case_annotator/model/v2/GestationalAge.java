package org.monarchinitiative.hpo_case_annotator.model.v2;

public interface GestationalAge {

    static GestationalAge of(Integer weeks, Integer days) {
        return new GestationalAgeDefault(weeks, days);
    }

    Integer getWeeks();

    Integer getDays();

}
