package org.monarchinitiative.hpo_case_annotator.forms.mining;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import org.monarchinitiative.phenol.ontology.data.TermId;

class ObservableMinedTerm {

    static Callback<ObservableMinedTerm, Observable[]> EXTRACTOR = omt -> new Observable[]{omt.isApproved, omt.isExcluded};

    private final TermId termId;
    private final String label;
    private final BooleanProperty isExcluded = new SimpleBooleanProperty();
    private final BooleanProperty isApproved = new SimpleBooleanProperty();

    public ObservableMinedTerm(TermId termId, String label, boolean isExcluded) {
        this.termId = termId;
        this.label = label;
        this.isExcluded.setValue(isExcluded);
    }

    BooleanProperty isExcludedProperty() {
        return isExcluded;
    }
    BooleanProperty isApprovedProperty() {
        return isApproved;
    }

    TermId getTermId() {
        return this.termId;
    }

    String getLabel() {
        return this.label;
    }
}
