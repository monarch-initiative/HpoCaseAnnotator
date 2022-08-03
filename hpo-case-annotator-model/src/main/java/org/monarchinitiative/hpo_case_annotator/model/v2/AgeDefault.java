package org.monarchinitiative.hpo_case_annotator.model.v2;

record AgeDefault(Integer years, Integer months, Integer days) implements Age {

    @Override
    public Integer getYears() {
        return years;
    }

    @Override
    public Integer getMonths() {
        return months;
    }

    @Override
    public Integer getDays() {
        return days;
    }

}
