package org.monarchinitiative.hpo_case_annotator.forms.tree.base;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseOntologyClassTreeItem<T extends BaseOntologyClass> extends TreeItem<T> {

    private final Ontology ontology;
    private boolean childrenWereAdded;

    protected BaseOntologyClassTreeItem(Ontology ontology, T value) {
        super(value);
        this.ontology = ontology;
    }

    protected abstract TreeItem<T> treeItemForTerm(Ontology ontology, Term term);


    @Override
    public boolean isLeaf() {
        return OntologyAlgorithm.getChildTerms(ontology, getValue().id(), false).isEmpty();
    }

    @Override
    public ObservableList<TreeItem<T>> getChildren() {
        if (!childrenWereAdded) {
            ObservableList<TreeItem<T>> children = OntologyAlgorithm.getChildTerms(ontology, getValue().id(), false).stream()
                    .map(ontology.getTermMap()::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Term::getName))
                    .map(term -> treeItemForTerm(ontology, term))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            super.getChildren().setAll(children);
            childrenWereAdded = true;
        }

        return super.getChildren();
    }
}
