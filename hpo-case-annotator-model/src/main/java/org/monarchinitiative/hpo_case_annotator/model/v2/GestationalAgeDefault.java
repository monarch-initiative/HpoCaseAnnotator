package org.monarchinitiative.hpo_case_annotator.model.v2;

record GestationalAgeDefault(Integer weeks, Integer days) implements GestationalAge {

    @Override
    public Integer getWeeks() {
        return weeks;
    }

    @Override
    public Integer getDays() {
        return days;
    }

}
