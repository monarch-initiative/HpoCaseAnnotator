package org.monarchinitiative.hpo_case_annotator.model.v2;

public abstract class AgeBuilder<T extends Age> {

    protected int years = 0;
    protected int months = 0;
    protected int weeks = 0;
    protected int days = 0;
    protected boolean isGestational = false;

    protected AgeBuilder() {
    }

    public AgeBuilder<T> setYears(int years) {
        this.years = years;
        return this;
    }

    public AgeBuilder<T> setMonths(int months) {
        this.months = months;
        return this;
    }
    public AgeBuilder<T> setWeeks(int weeks) {
        this.weeks = weeks;
        return this;
    }
    public AgeBuilder<T> setDays(int days) {
        this.days = days;
        return this;
    }

    public AgeBuilder<T> setGestational() {
        this.isGestational = true;
        return this;
    }

    public AgeBuilder<T> setPostnatal() {
        this.isGestational = false;
        return this;
    }

    public abstract T build();

}
