package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Period;

public class ObservableAge {

    private final ObjectProperty<Integer> years = new SimpleObjectProperty<>(this, "years", null);
    private final ObjectProperty<Integer> months = new SimpleObjectProperty<>(this, "months", 0);
    private final ObjectProperty<Integer> days = new SimpleObjectProperty<>(this, "days", 0);
    private final ObjectBinding<Period> period = createPeriodBinding();

    public ObservableAge() {
    }

    public ObservableAge(int years, int months, int days) {
        this.years.set(years);
        this.months.set(months);
        this.days.set(days);
    }

    private ObjectBinding<Period> createPeriodBinding() {
        return Bindings.createObjectBinding(() -> {
            Integer y = years.get();
            Integer m = months.get();
            Integer d = days.get();
            if (y == null || m == null || d == null) {
                return null;
            } else {
                return Period.of(y, m, d);
            }
        }, years, months, days);
    }

    public Integer getYears() {
        return years.get();
    }

    public void setYears(Integer years) {
        this.years.set(years);
    }

    public ObjectProperty<Integer> yearsProperty() {
        return years;
    }

    public Integer getMonths() {
        return months.get();
    }

    public void setMonths(Integer months) {
        this.months.set(months);
    }

    public ObjectProperty<Integer> monthsProperty() {
        return months;
    }

    public Integer getDays() {
        return days.get();
    }

    public void setDays(Integer days) {
        this.days.set(days);
    }

    public ObjectProperty<Integer> daysProperty() {
        return days;
    }

    public ObjectBinding<Period> period() {
        return period;
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
