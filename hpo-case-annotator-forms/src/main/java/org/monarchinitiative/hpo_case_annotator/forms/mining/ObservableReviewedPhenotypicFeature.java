package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.core.mining.MinedTerm;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

import java.util.stream.Stream;

public class ObservableReviewedPhenotypicFeature extends ObservablePhenotypicFeature implements ReviewedPhenotypicFeature, MinedTerm {

    public static final Callback<ObservableReviewedPhenotypicFeature, Observable[]> EXTRACTOR = item -> {
        Observable[] parent = ObservablePhenotypicFeature.EXTRACTOR.call(item);
        Observable[] current = new Observable[]{item.reviewStatus};

        // Concatenate the parent and the current.
        Observable[] total = new Observable[current.length + parent.length];
        System.arraycopy(parent, 0, total, 0, parent.length);
        System.arraycopy(current, 0, total, parent.length, current.length);
        return total;
    };

    private final int start;
    private final int end;
    private final ObjectProperty<ReviewStatus> reviewStatus = new SimpleObjectProperty<>(this, "reviewStatus", ReviewStatus.UNREVIEWED);

    public ObservableReviewedPhenotypicFeature(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public ObservableReviewedPhenotypicFeature(int start, int end, ReviewedPhenotypicFeature phenotypicFeature) {
        super(phenotypicFeature);
        this.start = start;
        this.end = end;
        if (phenotypicFeature != null)
            reviewStatus.set(phenotypicFeature.getReviewStatus());
    }

    @Override
    public ReviewStatus getReviewStatus() {
        return reviewStatus.get();
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus.set(reviewStatus);
    }

    public ObjectProperty<ReviewStatus> reviewStatusProperty() {
        return reviewStatus;
    }

    @Override
    public Stream<Observable> observables() {
        return Stream.concat(Stream.of(reviewStatus), super.observables());
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public boolean isNegated() {
        return isExcluded();
    }

    @Override
    public String toString() {
        return "ReviewedObservablePhenotypicFeature{" +
                "start=" + start +
                ", end=" + end +
                ", reviewStatus=" + reviewStatus.get() +
                ", termId=" + getTermId() +
                ", label=" + getLabel() +
                ", excluded=" + isExcluded() +
                ", onset=" + getOnset() +
                ", resolution=" + getResolution() +
                '}';
    }
}
