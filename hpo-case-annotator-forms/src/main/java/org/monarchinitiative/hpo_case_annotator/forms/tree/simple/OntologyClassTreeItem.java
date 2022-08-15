package org.monarchinitiative.hpo_case_annotator.forms.tree.simple;

import javafx.scene.control.TreeItem;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClass;
import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClassTreeItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

public class OntologyClassTreeItem extends BaseOntologyClassTreeItem<SimpleOntologyClass> {

    public OntologyClassTreeItem(Ontology ontology, SimpleOntologyClass feature) {
        super(ontology, feature);
    }

    @Override
    protected TreeItem<SimpleOntologyClass> treeItemForTerm(Ontology ontology, Term term) {
        return new OntologyClassTreeItem(ontology, new SimpleOntologyClass(term.id(), term.getName()));
    }

}
