package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservablePhenotypicFeature {

    private final ObjectProperty<TermId> termId = new SimpleObjectProperty<>(this, "termId");
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");
    private final ObjectProperty<AgeRange> observationAge = new SimpleObjectProperty<>(this, "observationAge");

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

    public AgeRange getObservationAge() {
        return observationAge.get();
    }

    public void setObservationAge(AgeRange observationAge) {
        this.observationAge.set(observationAge);
    }

    public ObjectProperty<AgeRange> observationAgeProperty() {
        return observationAge;
    }

}
