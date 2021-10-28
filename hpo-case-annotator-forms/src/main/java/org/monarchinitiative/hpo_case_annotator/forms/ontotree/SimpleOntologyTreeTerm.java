package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import org.monarchinitiative.phenol.ontology.data.Term;

class SimpleOntologyTreeTerm extends OntologyTreeTermBase {

    static SimpleOntologyTreeTerm of(Term term) {
        return new SimpleOntologyTreeTerm(term);
    }

    private SimpleOntologyTreeTerm(Term term) {
        super(term);
    }

    @Override
    public String toString() {
        return "SimpleOntologyTreeTerm{} " + super.toString();
    }
}
