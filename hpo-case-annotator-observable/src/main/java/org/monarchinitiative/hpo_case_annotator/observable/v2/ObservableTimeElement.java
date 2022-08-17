package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.stream.Stream;

public class ObservableTimeElement implements TimeElement, ObservableItem {

    static final Callback<ObservableTimeElement, Stream<Observable>> EXTRACTOR = ote -> Stream.of(ote.timeElementCase, ote.gestationalAge, ote.age, ote.ageRange, ote.ontologyClass);

    public static ObservableTimeElement defaultInstance() {
        ObservableTimeElement instance = new ObservableTimeElement();
        instance.setTimeElementCase(TimeElementCase.AGE);
        instance.setGestationalAge(ObservableGestationalAge.defaultInstance());
        instance.setAge(ObservableAge.defaultInstance());
        instance.setAgeRange(ObservableAgeRange.defaultInstance());
        return instance;
    }

    private final ObjectProperty<TimeElement.TimeElementCase> timeElementCase = new SimpleObjectProperty<>(this, "timeElementCase");
    private final ObjectProperty<ObservableGestationalAge> gestationalAge = new SimpleObjectProperty<>(this, "gestationalAge");
    private final ObjectProperty<ObservableAge> age = new SimpleObjectProperty<>(this, "age");
    private final ObjectProperty<ObservableAgeRange> ageRange = new SimpleObjectProperty<>(this, "ageRange");
    private final ObjectProperty<TermId> ontologyClass = new SimpleObjectProperty<>(this, "ontologyClass");

    public ObservableTimeElement() {
    }

    public ObservableTimeElement(TimeElement timeElement) {
        if (timeElement != null) {
            TimeElementCase tec = timeElement.getTimeElementCase();
            timeElementCase.set(tec);
            switch (tec) {
                case GESTATIONAL_AGE -> gestationalAge.set(new ObservableGestationalAge(timeElement.getGestationalAge()));
                case AGE -> age.set(new ObservableAge(timeElement.getAge()));
                case AGE_RANGE -> ageRange.set(new ObservableAgeRange(timeElement.getAgeRange()));
                case ONTOLOGY_CLASS -> ontologyClass.set(timeElement.getOntologyClass());
            }
        }
    }

    @Override
    public TimeElementCase getTimeElementCase() {
        return timeElementCase.get();
    }

    public void setTimeElementCase(TimeElementCase timeElementCase) {
        this.timeElementCase.set(timeElementCase);
    }

    public ObjectProperty<TimeElementCase> timeElementCaseProperty() {
        return timeElementCase;
    }

    @Override
    public ObservableGestationalAge getGestationalAge() {
        return gestationalAge.get();
    }

    public void setGestationalAge(ObservableGestationalAge gestationalAge) {
        this.gestationalAge.set(gestationalAge);
    }

    public ObjectProperty<ObservableGestationalAge> gestationalAgeProperty() {
        return gestationalAge;
    }

    @Override
    public ObservableAge getAge() {
        return age.get();
    }

    public void setAge(ObservableAge age) {
        this.age.set(age);
    }

    public ObjectProperty<ObservableAge> ageProperty() {
        return age;
    }

    @Override
    public ObservableAgeRange getAgeRange() {
        return ageRange.get();
    }

    public void setAgeRange(ObservableAgeRange ageRange) {
        this.ageRange.set(ageRange);
    }

    public ObjectProperty<ObservableAgeRange> ageRangeProperty() {
        return ageRange;
    }

    @Override
    public TermId getOntologyClass() {
        return ontologyClass.get();
    }

    public void setOntologyClass(TermId ontologyClass) {
        this.ontologyClass.set(ontologyClass);
    }

    public ObjectProperty<TermId> ontologyClassProperty() {
        return ontologyClass;
    }

    @Override
    public Stream<Observable> dependencies() {
        return EXTRACTOR.call(this);
    }

    @Override
    public String toString() {
        return "ObservableTimeElement{" +
                "timeElementCase=" + timeElementCase.get() +
                ", gestationalAge=" + gestationalAge.get() +
                ", age=" + age.get() +
                ", ageRange=" + ageRange.get() +
                ", ontologyClass=" + ontologyClass.get() +
                '}';
    }
}
