package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.model.SelectableOntologyTerm;
import org.monarchinitiative.hpo_case_annotator.forms.model.SelectionStatus;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

class SelectableOntologyTreeTerm extends OntologyTreeTermBase implements SelectableOntologyTerm {

    private final BooleanProperty included = new SimpleBooleanProperty(this, "included", false);
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded", false);
    private final ObjectProperty<SelectionStatus> selectionStatus = new SimpleObjectProperty<>(this, "selectionStatus", SelectionStatus.NA);

    private SelectableOntologyTreeTerm(Term term) {
        super(term);
        ObjectBinding<SelectionStatus> condensate = Bindings.createObjectBinding(() -> {
                    if (included.get()) {
                        return SelectionStatus.INCLUDED;
                    } else if (excluded.get()) {
                        return SelectionStatus.EXCLUDED;
                    } else {
                        return SelectionStatus.NA;
                    }
                },
                includedProperty(), excludedProperty());
        selectionStatus.bind(condensate);
    }

    static SelectableOntologyTreeTerm of(Term term) {
        return new SelectableOntologyTreeTerm(term);
    }

    public BooleanProperty includedProperty() {
        return included;
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }

    @Override
    public SelectionStatus selectionStatus() {
        return selectionStatus.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectableOntologyTreeTerm that = (SelectableOntologyTreeTerm) o;
        return Objects.equals(term, that.term) && selectionStatus == that.selectionStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, selectionStatus);
    }

    @Override
    public String toString() {
        return "SelectableOntologyTreeTerm{" +
                "term=" + term +
                ", selectionStatus=" + selectionStatus.get() +
                '}';
    }
}
