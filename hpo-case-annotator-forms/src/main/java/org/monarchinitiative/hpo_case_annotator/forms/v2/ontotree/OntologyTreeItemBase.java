package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Comparator;
import java.util.stream.Collectors;

abstract class OntologyTreeItemBase<T extends OntologyTreeTermBase> extends TreeItem<T> {

    private final Ontology ontology;

    private ObservableList<TreeItem<T>> children;

    protected OntologyTreeItemBase(Ontology ontology, T value) {
        super(value);
        this.ontology = ontology;
    }

    protected abstract TreeItem<T> treeItemForTerm(Ontology ontology, Term term);


    @Override
    public boolean isLeaf() {
        return OntologyAlgorithm.getChildTerms(ontology, getValue().term().id(), false).isEmpty();
    }

    @Override
    public ObservableList<TreeItem<T>> getChildren() {
        if (children == null) {
            children = OntologyAlgorithm.getChildTerms(ontology, getValue().term().id(), false).stream()
                    .map(ontology.getTermMap()::get)
                    .sorted(Comparator.comparing(Term::getName))
                    .map(term -> treeItemForTerm(ontology, term))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            super.getChildren().setAll(children);
        }

        return super.getChildren();
    }
}
