package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.stream.Stream;

public class ObservablePhenotypicFeature extends DeepObservable implements PhenotypicFeature {

    public static final Callback<ObservablePhenotypicFeature, Observable[]> EXTRACTOR = obs -> new Observable[]{
            obs.excluded,
            obs.onset,
            obs.resolution
    };

    private TermId termId;
    private String label;
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");
    private final ObjectProperty<ObservableTimeElement> onset = new SimpleObjectProperty<>(this, "onset");
    private final ObjectProperty<ObservableTimeElement> resolution = new SimpleObjectProperty<>(this, "resolution");

    public ObservablePhenotypicFeature() {
    }

    public ObservablePhenotypicFeature(PhenotypicFeature phenotypicFeature) {
        if (phenotypicFeature != null) {
            termId = phenotypicFeature.id();
            label = phenotypicFeature.getLabel();
            excluded.set(phenotypicFeature.isExcluded());

            if (phenotypicFeature.getOnset() != null)
                onset.set(new ObservableTimeElement(phenotypicFeature.getOnset()));

            if (phenotypicFeature.getResolution() != null)
                resolution.set(new ObservableTimeElement(phenotypicFeature.getResolution()));
        }
    }

    @Override
    public TermId id() {
        return termId;
    }

    public TermId getTermId() {
        return termId;
    }

    public void setTermId(TermId termId) {
        this.termId = termId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
    protected Stream<Property<? extends Observable>> objectProperties() {
        return Stream.of(onset, resolution);
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.of(EXTRACTOR.call(this));
    }

    @Override
    public String toString() {
        return "ObservablePhenotypicFeature{" +
                "termId=" + termId +
                ", label=" + label +
                ", excluded=" + excluded.get() +
                ", onset=" + onset.get() +
                ", resolution=" + resolution.get() +
                '}';
    }
}
