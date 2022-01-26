package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SelectableOntologyTreeController extends BaseOntologyTreeBrowser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectableOntologyTreeController.class);

    @FXML
    private VBox section;
    @FXML
    private TreeView<SelectableOntologyTreeTerm> ontologyTreeView;

    @FXML
    private void initialize() {
        ontologyTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ontology.addListener((obs, old, novel) -> {
            if (old != null) unbindOntology();
            if (novel != null) bindOntology(novel);
        });
    }

    private void unbindOntology() {
        section.setDisable(true);
        ontologyTreeView.setRoot(null);
        ontologyTreeView.setCellFactory(null);
    }

    private void bindOntology(Ontology ontology) {
        section.setDisable(false);
        TreeItem<SelectableOntologyTreeTerm> root = simpleRoot(ontology);
        ontologyTreeView.setRoot(root);
        ontologyTreeView.setCellFactory(tv -> SelectableOntologyTreeCell.of());
    }

    private static TreeItem<SelectableOntologyTreeTerm> simpleRoot(Ontology ontology) {
        Term root = ontology.getTermMap().get(ontology.getRootTermId());
        return SelectableOntologyTreeItem.of(ontology, root);
    }

    public ChangeListener<ObservablePhenotypicFeature> phenotypeDescriptionChangeListener() {
        return (obs, old, novel) -> {
            if (novel != null)
                navigateToTermId(novel.getTermId());
        };
    }

    public void navigateToTermId(TermId termId) {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            // shouldn't happen but let's be 100% sure
            return;
        if (OntologyAlgorithm.existsPath(ontology, termId, ontology.getRootTermId())) {
            // find root -> term path through the tree
            Stack<TermId> stack = constructPath(ontology, termId);

            // expand tree nodes in top -> down direction
            List<TreeItem<SelectableOntologyTreeTerm>> children = ontologyTreeView.getRoot().getChildren();
            stack.pop(); // get rid of 'All' node which is hidden
            TreeItem<SelectableOntologyTreeTerm> target = ontologyTreeView.getRoot();
            while (!stack.empty()) {
                TermId current = stack.pop();
                for (TreeItem<SelectableOntologyTreeTerm> child : children) {
                    if (child.getValue().term().id().equals(current)) {
                        child.setExpanded(true);
                        target = child;
                        children = child.getChildren();
                        break;
                    }
                }
            }
            ontologyTreeView.requestFocus();
            ontologyTreeView.getSelectionModel().select(target);
            ontologyTreeView.scrollTo(ontologyTreeView.getSelectionModel().getSelectedIndex() - 5);
        } else {
            LOGGER.warn("Unable to find the path from {} to {}", ontology.getRootTermId(), termId);
        }
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    /*
    private static TreeItem<SelectableOntologyTreeTerm> generateTheEntireOntologyGraph(Ontology ontology) {
        TermId rootTermId = ontology.getRootTermId();
        Map<TermId, Term> termMap = ontology.getTermMap();
        // takes arount .3621 seconds to create the entire tree
        Queue<TreeItem<SelectableOntologyTreeTerm>> queue = new LinkedList<>();
        TreeItem<SelectableOntologyTreeTerm> root = new TreeItem<>(SelectableOntologyTreeTerm.of(termMap.get(rootTermId)));
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeItem<SelectableOntologyTreeTerm> treeItem = queue.remove();
            Term term = treeItem.getValue().term();
            TermId termId = term.getId();
            List<TreeItem<SelectableOntologyTreeTerm>> children = OntologyAlgorithm.getChildTerms(ontology, termId, false).stream()
                    .map(tid -> new TreeItem<>(SelectableOntologyTreeTerm.of(termMap.get(tid))))
                    .toList();
            treeItem.getChildren().addAll(children);
            queue.addAll(children);
        }

        return root;
    }
     */
}
