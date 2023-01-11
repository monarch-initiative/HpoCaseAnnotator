package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.observable.deep.DeepObservable;

import java.util.stream.Stream;

public class ObservableTimeElement extends DeepObservable implements TimeElement {

    // TODO - simplify API, hide TimeElementCase setter
    public static final Callback<ObservableTimeElement, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.timeElementCase, obs.gestationalAge, obs.age, obs.ageRange, obs.ontologyClass};

    // Time element case must not be null when starting.
    private final ObjectProperty<TimeElement.TimeElementCase> timeElementCase = new SimpleObjectProperty<>(this, "timeElementCase", TimeElementCase.AGE);
    private final ObjectProperty<ObservableGestationalAge> gestationalAge = new SimpleObjectProperty<>(this, "gestationalAge");
    private final ObjectProperty<ObservableAge> age = new SimpleObjectProperty<>(this, "age");
    private final ObjectProperty<ObservableAgeRange> ageRange = new SimpleObjectProperty<>(this, "ageRange");
    private final ObjectProperty<OntologyClass> ontologyClass = new SimpleObjectProperty<>(this, "ontologyClass");

    public ObservableTimeElement() {
        super();
    }

    public ObservableTimeElement(TimeElement timeElement) {
        super();
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
    public OntologyClass getOntologyClass() {
        return ontologyClass.get();
    }

    public void setOntologyClass(OntologyClass ontologyClass) {
        this.ontologyClass.set(ontologyClass);
    }

    public ObjectProperty<OntologyClass> ontologyClassProperty() {
        return ontologyClass;
    }

    @Override
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(gestationalAge, age, ageRange);
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
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
