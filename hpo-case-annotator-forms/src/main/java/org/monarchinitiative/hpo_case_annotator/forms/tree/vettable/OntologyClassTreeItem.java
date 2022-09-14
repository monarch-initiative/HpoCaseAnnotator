package org.monarchinitiative.hpo_case_annotator.forms.tree.vettable;


import javafx.scene.control.TreeItem;
import org.monarchinitiative.hpo_case_annotator.forms.tree.VettableOntologyClass;
import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClassTreeItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Objects;

public class OntologyClassTreeItem extends BaseOntologyClassTreeItem<VettableOntologyClass> {
    private final OntologyClassTreeItemManager treeItemManager;

    public OntologyClassTreeItem(VettableOntologyClass item, Ontology ontology, OntologyClassTreeItemManager treeItemManager) {
        super(ontology, item);
        this.treeItemManager = Objects.requireNonNull(treeItemManager);
    }

    @Override
    protected TreeItem<VettableOntologyClass> treeItemForTerm(Ontology ontology, Term term) {
        return new OntologyClassTreeItem(treeItemManager.getOntologyClassForTerm(term), ontology, treeItemManager);
    }

}
