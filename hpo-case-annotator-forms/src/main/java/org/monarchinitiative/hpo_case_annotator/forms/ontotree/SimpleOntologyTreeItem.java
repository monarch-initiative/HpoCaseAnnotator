package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

class SimpleOntologyTreeItem extends OntologyTreeItemBase<SimpleOntologyTreeTerm> {

    static SimpleOntologyTreeItem of(Ontology ontology, Term term) {
        SimpleOntologyTreeTerm treeTerm = SimpleOntologyTreeTerm.of(term);
        return new SimpleOntologyTreeItem(ontology, treeTerm);
    }

    private SimpleOntologyTreeItem(Ontology ontology, SimpleOntologyTreeTerm term) {
        super(ontology, term);
    }

    @Override
    protected TreeItem<SimpleOntologyTreeTerm> treeItemForTerm(Ontology ontology, Term term) {
        return of(ontology, term);
    }

}
