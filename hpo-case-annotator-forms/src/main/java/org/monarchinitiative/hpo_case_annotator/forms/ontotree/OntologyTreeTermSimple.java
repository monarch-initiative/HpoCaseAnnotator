package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.forms.model.OntologyTreeTerm;
import org.monarchinitiative.hpo_case_annotator.forms.model.SelectionStatus;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

public class OntologyTreeTermSimple implements OntologyTreeTerm {

    private final Term term;
    private final BooleanProperty included = new SimpleBooleanProperty(this, "included", false);
    private final BooleanProperty excluded = new SimpleBooleanProperty(this, "excluded", false);
    private final ObjectProperty<SelectionStatus> selectionStatus = new SimpleObjectProperty<>(this, "selectionStatus", SelectionStatus.NA);

    private OntologyTreeTermSimple(Term term) {
        this.term = term;
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

    public static OntologyTreeTermSimple of(Term term) {
        return new OntologyTreeTermSimple(term);
    }

    public boolean isIncluded() {
        return included.get();
    }

    public BooleanProperty includedProperty() {
        return included;
    }

    public boolean isExcluded() {
        return excluded.get();
    }

    public BooleanProperty excludedProperty() {
        return excluded;
    }

    @Override
    public SelectionStatus selectionStatus() {
        return selectionStatus.get();
    }

    public ObjectProperty<SelectionStatus> selectionStatusProperty() {
        return selectionStatus;
    }

    @Override
    public Term getTerm() {
        return term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OntologyTreeTermSimple that = (OntologyTreeTermSimple) o;
        return Objects.equals(term, that.term) && selectionStatus == that.selectionStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, selectionStatus);
    }

    @Override
    public String toString() {
        return "OntologyTreeTermImpl{" +
                "term=" + term +
                ", selectionStatus=" + selectionStatus.get() +
                '}';
    }
}
