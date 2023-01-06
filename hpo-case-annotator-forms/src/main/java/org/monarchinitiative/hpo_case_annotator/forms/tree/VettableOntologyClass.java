package org.monarchinitiative.hpo_case_annotator.forms.tree;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClass;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class VettableOntologyClass extends BaseOntologyClass implements Observable {

    private final ObjectProperty<VettingStatus> selectionStatus = new SimpleObjectProperty<>();

    public VettableOntologyClass(TermId termId, String label) {
        super(termId, label);
        this.selectionStatus.set(VettingStatus.INDETERMINATE);
    }

    public VettingStatus getSelectionStatus() {
        return selectionStatus.get();
    }

    public ObjectProperty<VettingStatus> selectionStatusProperty() {
        return selectionStatus;
    }

    public void setSelectionStatus(VettingStatus vettingStatus) {
        this.selectionStatus.set(vettingStatus);
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
        return "VettableOntologyClass{" +
                "id=" + id().getValue() +
                ", label=" + getLabel() +
                ", selectionStatus=" + selectionStatus.get() +
                '}';
    }
}
