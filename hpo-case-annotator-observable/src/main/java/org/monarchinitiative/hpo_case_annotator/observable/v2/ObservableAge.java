package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;

public class ObservableAge implements Age {

    public static ObservableAge defaultInstance() {
        return new ObservableAge();
    }

    // Years, months, and days are nullabe, hence ObjectProperty.
    private final ObjectProperty<Integer> years = new SimpleObjectProperty<>(this, "years");
    private final ObjectProperty<Integer> months = new SimpleObjectProperty<>(this, "months");
    private final ObjectProperty<Integer> days = new SimpleObjectProperty<>(this, "days");

    public ObservableAge() {
    }

    public ObservableAge(Age age) {
        if (age != null) {
            years.set(age.getYears());
            months.set(age.getMonths());
            days.set(age.getDays());
        }
    }

    @Override
    public Integer getYears() {
        return years.get();
    }

    public void setYears(Integer years) {
        this.years.setValue(years);
    }

    public ObjectProperty<Integer> yearsProperty() {
        return years;
    }

    @Override
    public Integer getMonths() {
        return months.get();
    }

    public void setMonths(Integer months) {
        this.months.setValue(months);
    }

    public ObjectProperty<Integer> monthsProperty() {
        return months;
    }

    @Override
    public Integer getDays() {
        return days.get();
    }

    public void setDays(Integer days) {
        this.days.setValue(days);
    }

    public ObjectProperty<Integer> daysProperty() {
        return days;
    }

    @Override
    public String toString() {
        return "ObservableAge{" +
                "years=" + years.get() +
                ", months=" + months.get() +
                ", days=" + days.get() +
                '}';
    }
}
