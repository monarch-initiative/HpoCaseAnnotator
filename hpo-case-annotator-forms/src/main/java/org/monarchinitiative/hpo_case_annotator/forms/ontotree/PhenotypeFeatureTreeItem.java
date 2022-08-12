package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

class PhenotypeFeatureTreeItem extends TreeItem<PhenotypeFeatureItem> {
    private final Ontology ontology;
    private final PhenotypeFeatureTreeItemManager treeItemManager;
    private boolean childrenWereAdded;

    PhenotypeFeatureTreeItem(PhenotypeFeatureItem item, Ontology ontology, PhenotypeFeatureTreeItemManager treeItemManager) {
        super(item);
        this.ontology = Objects.requireNonNull(ontology);
        this.treeItemManager = Objects.requireNonNull(treeItemManager);
    }

    @Override
    public boolean isLeaf() {
        return OntologyAlgorithm.getChildTerms(ontology, getValue().getTermId(), false).isEmpty();
    }

    @Override
    public ObservableList<TreeItem<PhenotypeFeatureItem>> getChildren() {
        if (!childrenWereAdded) {
            List<PhenotypeFeatureTreeItem> children = OntologyAlgorithm.getChildTerms(ontology, getValue().getTermId(), false).stream()
                    .map(ontology.getTermMap()::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Term::getName))
                    .map(this::treeItemForTerm)
                    .toList();
            super.getChildren().addAll(children);
            childrenWereAdded = true;
        }

        return super.getChildren();
    }

    private PhenotypeFeatureTreeItem treeItemForTerm(Term term) {
        return new PhenotypeFeatureTreeItem(treeItemManager.getOntoItem(term), ontology, treeItemManager);
    }

}
