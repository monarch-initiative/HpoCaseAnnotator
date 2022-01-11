package org.monarchinitiative.hpo_case_annotator.forms.observable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservablePhenotypicFeature {

    private final ObjectProperty<TermId> termId = new SimpleObjectProperty<>(this, "termId");
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");
    private final ObjectProperty<ObservableAgeRange> observationAge = new SimpleObjectProperty<>(this, "observationAge");

    public TermId getTermId() {
        return termId.get();
    }

    public void setTermId(TermId termId) {
        this.termId.set(termId);
    }

    public ObjectProperty<TermId> termIdProperty() {
        return termId;
    }

    public boolean isExcluded() {
        return excluded.get();
    }

    public void setExcluded(boolean excluded) {
        this.excluded.set(excluded);
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }

    public ObservableAgeRange getObservationAge() {
        return observationAge.get();
    }

    public void setObservationAge(ObservableAgeRange observationAge) {
        this.observationAge.set(observationAge);
    }

    public ObjectProperty<ObservableAgeRange> observationAgeProperty() {
        return observationAge;
    }

}
