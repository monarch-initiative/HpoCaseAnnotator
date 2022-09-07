package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;

import java.util.stream.Stream;

public class ObservableDiseaseStatus implements DiseaseStatus, ObservableItem {

    static final Callback<ObservableDiseaseStatus, Stream<Observable>> EXTRACTOR = oa -> Stream.of(oa.excluded);

    private DiseaseIdentifier diseaseId;
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded");

    public ObservableDiseaseStatus() {
    }

    public ObservableDiseaseStatus(DiseaseStatus diseaseStatus) {
        if (diseaseStatus != null) {
            if (diseaseStatus.getDiseaseId() != null)
                diseaseId = new DiseaseIdentifier(diseaseStatus.getDiseaseId());

            excluded.set(diseaseStatus.isExcluded());
        }
    }

    @Override
    public DiseaseIdentifier getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(DiseaseIdentifier diseaseId) {
        this.diseaseId = diseaseId;
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
    public Stream<Observable> dependencies() {
        return EXTRACTOR.call(this);
    }

    @Override
    public String toString() {
        return "ObservableDiseaseStatus{" +
                "diseaseId=" + diseaseId +
                ", excluded=" + excluded.get() +
                '}';
    }
}
