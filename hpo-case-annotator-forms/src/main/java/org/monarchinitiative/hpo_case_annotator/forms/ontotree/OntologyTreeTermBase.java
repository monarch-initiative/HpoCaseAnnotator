package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import org.monarchinitiative.hpo_case_annotator.forms.model.OntologyTerm;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

abstract class OntologyTreeTermBase implements OntologyTerm {

    protected final Term term;

    public OntologyTreeTermBase(Term term) {
        this.term = term;
    }

    public Term term() {
        return term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OntologyTreeTermBase that = (OntologyTreeTermBase) o;
        return Objects.equals(term, that.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term);
    }

    @Override
    public String toString() {
        return "OntologyTreeTermBase{" +
                "term=" + term +
                '}';
    }
}
