package org.monarchinitiative.hpo_case_annotator.model.v2;

record AgeDefault(boolean isGestational, int years, int months, int weeks, int days) implements Age {

    @Override
    public int getYears() {
        return years;
    }

    @Override
    public int getMonths() {
        return months;
    }

    @Override
    public int getWeeks() {
        return weeks;
    }

    @Override
    public int getDays() {
        return days;
    }

}
