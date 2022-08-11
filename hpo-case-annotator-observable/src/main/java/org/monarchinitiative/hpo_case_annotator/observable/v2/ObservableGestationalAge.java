package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;

public class ObservableGestationalAge implements GestationalAge {

    public static ObservableGestationalAge defaultInstance() {
        return new ObservableGestationalAge();
    }

    // Weeks and days are nullabe, hence ObjectProperty.
    private final ObjectProperty<Integer> weeks = new SimpleObjectProperty<>(this, "weeks");
    private final ObjectProperty<Integer> days = new SimpleObjectProperty<>(this, "days");

    public ObservableGestationalAge() {
    }

    public ObservableGestationalAge(GestationalAge gestationalAge) {
        if (gestationalAge != null) {
            weeks.set(gestationalAge.getWeeks());
            days.set(gestationalAge.getDays());
        }
    }

    public Integer getWeeks() {
        return weeks.get();
    }

    public ObjectProperty<Integer> weeksProperty() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks.set(weeks);
    }

    public Integer getDays() {
        return days.get();
    }

    public ObjectProperty<Integer> daysProperty() {
        return days;
    }

    public void setDays(Integer days) {
        this.days.set(days);
    }

    @Override
    public String toString() {
        return "ObservableGestationalAge{" +
                "weeks=" + weeks.get() +
                ", days=" + days.get() +
                '}';
    }
}
