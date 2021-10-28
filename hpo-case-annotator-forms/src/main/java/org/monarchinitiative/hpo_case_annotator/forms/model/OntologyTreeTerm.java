package org.monarchinitiative.hpo_case_annotator.forms.model;

import org.monarchinitiative.phenol.ontology.data.Term;

public interface OntologyTreeTerm {

    Term getTerm();

    SelectionStatus selectionStatus();

}
