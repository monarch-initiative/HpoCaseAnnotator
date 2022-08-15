package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.phenol.ontology.data.TermId;

class ObservableMinedTerm {

    static Callback<ObservableMinedTerm, Observable[]> EXTRACTOR = omt -> new Observable[]{omt.reviewStatus, omt.isExcluded};

    private final TermId termId;
    private final String label;
    private final int start;
    private final int end;
    private final BooleanProperty isExcluded = new SimpleBooleanProperty(this, "isExcluded");
    private final ObjectProperty<ReviewStatus> reviewStatus = new SimpleObjectProperty<>(this, "reviewStatus");

    public ObservableMinedTerm(TermId termId, String label, int start, int end, boolean isExcluded) {
        this.termId = termId;
        this.label = label;
        this.start = start;
        this.end = end;
        this.isExcluded.setValue(isExcluded);
        this.reviewStatus.set(ReviewStatus.UNREVIEWED);
    }
    TermId getTermId() {
        return this.termId;
    }

    String getLabel() {
        return this.label;
    }

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
    }

    BooleanProperty isExcludedProperty() {
        return isExcluded;
    }
    ObjectProperty<ReviewStatus> reviewStatusProperty() {
        return reviewStatus;
    }

    ReviewStatus getReviewStatus() {
        return reviewStatus.get();
    }

    void setReviewStatus(ReviewStatus status) {
        reviewStatus.set(status);
    }

    @Override
    public String toString() {
        return "ObservableMinedTerm{" +
                "termId=" + termId +
                ", label='" + label + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", isExcluded=" + isExcluded.get() +
                ", reviewStatus=" + reviewStatus.get() +
                '}';
    }
}
