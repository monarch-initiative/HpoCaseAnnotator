package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;


public class OntologyTreeItem extends TreeItem<OntologyTreeTermSimple> {

    private final Ontology ontology;

    private ObservableList<TreeItem<OntologyTreeTermSimple>> children;

    public static OntologyTreeItem of(Ontology ontology, Term term) {
        OntologyTreeTermSimple treeTerm = OntologyTreeTermSimple.of(term);
        return new OntologyTreeItem(ontology, treeTerm);
    }

    private OntologyTreeItem(Ontology ontology, OntologyTreeTermSimple value) {
        super(value);
        this.ontology = ontology;

    }
    @Override
    public boolean isLeaf() {
        return OntologyAlgorithm.getChildTerms(ontology, getValue().getTerm().getId(), false).isEmpty();
    }

    @Override
    public ObservableList<TreeItem<OntologyTreeTermSimple>> getChildren() {
        if (children == null) {
            children = FXCollections.observableArrayList();
            Set<Term> childrenTerms = OntologyAlgorithm.getChildTerms(ontology, getValue().getTerm().getId(), false).stream()
                    .map(ontology.getTermMap()::get)
                    .collect(Collectors.toUnmodifiableSet());

            children = childrenTerms.stream()
                    .sorted(Comparator.comparing(Term::getName))
                    .map(term -> of(ontology, term))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            super.getChildren().setAll(children);
        }

        return super.getChildren();
    }

}
