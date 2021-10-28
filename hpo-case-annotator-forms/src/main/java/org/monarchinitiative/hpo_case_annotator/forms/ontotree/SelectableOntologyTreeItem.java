package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;


class SelectableOntologyTreeItem extends OntologyTreeItemBase<SelectableOntologyTreeTerm> {

    static SelectableOntologyTreeItem of(Ontology ontology, Term term) {
        SelectableOntologyTreeTerm treeTerm = SelectableOntologyTreeTerm.of(term);
        return new SelectableOntologyTreeItem(ontology, treeTerm);
    }

    private SelectableOntologyTreeItem(Ontology ontology, SelectableOntologyTreeTerm value) {
        super(ontology, value);
    }

    @Override
    protected TreeItem<SelectableOntologyTreeTerm> treeItemForTerm(Ontology ontology, Term term) {
        return of(ontology, term);
    }

}
