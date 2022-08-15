package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * A construct identical to ontology class in Phenopacket schema, both v1 and v2. Essentially a CURIE and a label.
 */
public interface OntologyClass extends Identified {

    static OntologyClass of(Term term) {
        return of(term.id(), term.getName());
    }

    static OntologyClass of(TermId id, String label) {
        return new OntologyClassDefault(id, label);
    }

    default TermId getId() {
        return id();
    }

    String getLabel();

}
