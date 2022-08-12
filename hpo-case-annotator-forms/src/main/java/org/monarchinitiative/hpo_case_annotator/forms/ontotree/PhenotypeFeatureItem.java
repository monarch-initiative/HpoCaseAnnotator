package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class PhenotypeFeatureItem implements Observable {

    private final Term term;
    private final ObjectProperty<SelectionStatus> selectionStatus = new SimpleObjectProperty<>();

    public PhenotypeFeatureItem(Term term) {
        this.term = term;
        this.selectionStatus.set(SelectionStatus.INDETERMINATE);
    }

    public TermId getTermId() {
        return term.id();
    }

    public String getName() {
        return term.getName();
    }

    public SelectionStatus getSelectionStatus() {
        return selectionStatus.get();
    }

    public ObjectProperty<SelectionStatus> selectionStatusProperty() {
        return selectionStatus;
    }

    public void setSelectionStatus(SelectionStatus selectionStatus) {
        this.selectionStatus.set(selectionStatus);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        selectionStatus.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        selectionStatus.removeListener(listener);
    }

    @Override
    public String toString() {
        return "PhenotypeFeatureItem{" +
                "id=" + term.id().getValue() +
                ", name=" + term.getName() +
                ", selectionStatus=" + selectionStatus.get() +
                '}';
    }
}
