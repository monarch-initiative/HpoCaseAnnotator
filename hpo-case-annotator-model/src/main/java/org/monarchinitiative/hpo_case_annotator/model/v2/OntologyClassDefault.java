package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record OntologyClassDefault(TermId id, String label) implements OntologyClass {

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public TermId id() {
        return id;
    }
}
