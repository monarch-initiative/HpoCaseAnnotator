package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObservablePhenotypicFeature implements PhenotypicFeature {

    private final ObjectProperty<TermId> termId = new SimpleObjectProperty<>(this, "termId");
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");
    private final ObjectProperty<ObservableTimeElement> onset = new SimpleObjectProperty<>(this, "onset");
    private final ObjectProperty<ObservableTimeElement> resolution = new SimpleObjectProperty<>(this, "resolution");

    public ObservablePhenotypicFeature() {
    }

    public ObservablePhenotypicFeature(PhenotypicFeature phenotypicFeature) {
        if (phenotypicFeature != null) {
            termId.set(phenotypicFeature.id());

            excluded.set(phenotypicFeature.isExcluded());

            if (phenotypicFeature.getOnset() != null)
                onset.set(new ObservableTimeElement(phenotypicFeature.getOnset()));

            if (phenotypicFeature.getResolution() != null)
                resolution.set(new ObservableTimeElement(phenotypicFeature.getResolution()));
        }
    }

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
    public ObservableTimeElement getOnset() {
        return onset.get();
    }

    public void setOnset(ObservableTimeElement onset) {
        this.onset.set(onset);
    }

    public ObjectProperty<ObservableTimeElement> onsetProperty() {
        return onset;
    }

    @Override
    public ObservableTimeElement getResolution() {
        return resolution.get();
    }

    public void setResolution(ObservableTimeElement resolution) {
        this.resolution.set(resolution);
    }

    public ObjectProperty<ObservableTimeElement> resolutionProperty() {
        return resolution;
    }

    @Override
    public String toString() {
        return "ObservablePhenotypicFeature{" +
                "termId=" + termId.get() +
                ", excluded=" + excluded.get() +
                ", onset=" + onset.get() +
                ", resolution=" + resolution.get() +
                '}';
    }
}
