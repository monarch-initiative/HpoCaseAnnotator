package org.monarchinitiative.hpo_case_annotator.forms.tree.base;

import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.phenol.ontology.data.TermId;

public abstract class BaseOntologyClass implements OntologyClass {

    private final TermId termId;
    private final String label;

    protected BaseOntologyClass(TermId termId, String label) {
        this.termId = termId;
        this.label = label;
    }

    @Override
    public TermId id() {
        return termId;
    }

    @Override
    public TermId getId() {
        return termId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "BaseOntologyClass{" +
                "id=" + id().getValue() +
                ", label=" + getLabel() +
                '}';
    }
}
