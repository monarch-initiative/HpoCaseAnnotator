package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.v2.model.SelectableOntologyTerm;
import org.monarchinitiative.hpo_case_annotator.forms.v2.model.SelectionStatus;
import org.monarchinitiative.phenol.ontology.data.Term;

class SelectableOntologyTreeTerm extends OntologyTreeTermBase implements SelectableOntologyTerm {

    private final BooleanProperty included = new SimpleBooleanProperty(this, "included", false);
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded", false);
    private final ObjectProperty<SelectionStatus> selectionStatus = new SimpleObjectProperty<>(this, "selectionStatus", SelectionStatus.NA);

    private SelectableOntologyTreeTerm(Term term) {
        super(term);
        selectionStatus.bind(Bindings.createObjectBinding(() -> {
                    if (included.get()) {
                        return SelectionStatus.INCLUDED;
                    } else if (excluded.get()) {
                        return SelectionStatus.EXCLUDED;
                    } else {
                        return SelectionStatus.NA;
                    }
                },
                includedProperty(), excludedProperty()));

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

    static SelectableOntologyTreeTerm of(Term term) {
        return new SelectableOntologyTreeTerm(term);
    }

    @Override
    public String toString() {
        return "SelectableOntologyTreeTerm{" +
                "term=" + term +
                ", selectionStatus=" + selectionStatus.get() +
                '}';
    }
}
