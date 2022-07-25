package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.observable.Updateable;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservablePhenotypicFeature implements PhenotypicFeature, Updateable<PhenotypicFeature> {

    private final ObjectProperty<TermId> termId = new SimpleObjectProperty<>(this, "termId");
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");
    private final ObjectProperty<ObservableAgeRange> observationAge = new SimpleObjectProperty<>(this, "observationAge", new ObservableAgeRange());

    @Override
    public TermId id() {
        return termId.get();
    }

    public TermId getTermId() {
        return termId.get();
    }

    public void setTermId(TermId termId) {
        this.termId.set(termId);
    }

    public ObjectProperty<TermId> termIdProperty() {
        return termId;
    }

    @Override
    public boolean isExcluded() {
        return excluded.get();
    }

    public void setExcluded(boolean excluded) {
        this.excluded.set(excluded);
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }

    @Override
    public ObservableAgeRange getObservationAge() {
        return observationAge.get();
    }

    public void setObservationAge(ObservableAgeRange observationAge) {
        this.observationAge.set(observationAge);
    }

    @Override
    public void update(PhenotypicFeature data) {
        if (data == null) {
            termId.set(null);
            excluded.set(false);
            observationAge.get().update(null);
        } else {
            termId.set(data.id());
            excluded.set(data.isExcluded());
            observationAge.get().update(data.getObservationAge());
        }
    }

    @Override
    public String toString() {
        return "ObservablePhenotypicFeature{" +
                "termId=" + termId.get() +
                ", excluded=" + excluded.get() +
                ", observationAge=" + observationAge.get() +
                '}';
    }
}
