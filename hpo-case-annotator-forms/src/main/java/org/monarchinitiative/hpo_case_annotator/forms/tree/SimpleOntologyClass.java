package org.monarchinitiative.hpo_case_annotator.forms.tree;

import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClass;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class SimpleOntologyClass extends BaseOntologyClass {

    public SimpleOntologyClass(TermId termId, String label) {
        super(termId, label);
    }

    @Override
    public String toString() {
        return "SimpleOntologyClass{" +
                "id=" + id().getValue() +
                ", label=" + getLabel() +
                '}';
    }
}
