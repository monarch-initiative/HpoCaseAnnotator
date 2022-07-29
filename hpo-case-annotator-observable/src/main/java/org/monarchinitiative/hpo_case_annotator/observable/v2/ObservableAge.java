package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;

import java.time.Period;

public class ObservableAge implements Age, Updateable<Age> {

    private final BooleanProperty gestational = new SimpleBooleanProperty(this, "gestational");
    // Years, months, weeks, and days are nullabe, hence ObjectProperty
    private final ObjectProperty<Integer> years = new SimpleObjectProperty<>(this, "years");
    private final ObjectProperty<Integer> months = new SimpleObjectProperty<>(this, "months");
    private final ObjectProperty<Integer> weeks = new SimpleObjectProperty<>(this, "weeks");
    private final ObjectProperty<Integer> days = new SimpleObjectProperty<>(this, "days");
    private final ObjectBinding<Period> period = createPeriodBinding();

    public ObservableAge() {
    }

    public ObservableAge(Age age) {
        if (age != null) {
            gestational.set(age.isGestational());
            years.set(age.getYears());
            months.set(age.getMonths());
            weeks.set(age.getWeeks());
            days.set(age.getDays());
        }
    }

    private ObjectBinding<Period> createPeriodBinding() {
        return Bindings.createObjectBinding(() -> {
            int y = years.get();
            Integer m = months.get();
            Integer d = days.get();
            if (m == null || d == null) {
                return null;
            } else {
                return Period.of(y, m, d);
            }
        }, years, months, days);
    }

    @Override
    public boolean isGestational() {
        return gestational.get();
    }

    public void setGestational(boolean value) {
        this.gestational.set(value);
    }

    public BooleanProperty gestationalProperty() {
        return gestational;
    }

    @Override
    public int getYears() {
        return years.get();
    }

    public void setYears(Integer years) {
        this.years.setValue(years);
    }

    public ObjectProperty<Integer> yearsProperty() {
        return years;
    }

    @Override
    public int getWeeks() {
        return weeks.get();
    }

    public void setWeeks(Integer weeks) {
        this.weeks.setValue(weeks);
    }

    public ObjectProperty<Integer> weeksProperty() {
        return weeks;
    }

    @Override
    public int getMonths() {
        return months.get();
    }

    public void setMonths(Integer months) {
        this.months.setValue(months);
    }

    public ObjectProperty<Integer> monthsProperty() {
        return months;
    }

    @Override
    public int getDays() {
        return days.get();
    }

    public void setDays(Integer days) {
        this.days.setValue(days);
    }

    public ObjectProperty<Integer> daysProperty() {
        return days;
    }

    public ObjectBinding<Period> period() {
        return period;
    }

    @Override
    public void update(Age data) {
        if (data == null) {
            setYears(null);
            setMonths(null);
            setDays(null);
        } else {
            setYears(data.getYears());
            setMonths(data.getMonths());
            setDays(data.getDays());
        }
    }

    @Override
    public String toString() {
        return "ObservableAge{" +
                "isGestational=" + gestational.get() +
                ", years=" + years.get() +
                ", months=" + months.get() +
                ", weeks=" + weeks.get() +
                ", days=" + days.get() +
                '}';
    }
}
