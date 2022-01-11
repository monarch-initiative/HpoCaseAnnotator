package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public class OntologyTreeBrowserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectableOntologyTreeController.class);
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    @FXML
    private VBox ontologyBrowserBox;
    @FXML
    private TreeView<SimpleOntologyTreeTerm> ontologyTreeView;

    private static TreeItem<SimpleOntologyTreeTerm> simpleRoot(Ontology ontology) {
        Term root = ontology.getTermMap().get(ontology.getRootTermId());
        return SimpleOntologyTreeItem.of(ontology, root);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }


    @FXML
    private void initialize() {
        ontologyTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ontology.addListener(getOntologyChangeListener());
    }

    public ChangeListener<ObservablePhenotypicFeature> phenotypeDescriptionChangeListener() {
        return (obs, old, novel) -> {
            if (novel != null)
                navigateToTermId(novel.getTermId());
        };
    }

    private ChangeListener<Ontology> getOntologyChangeListener() {
        return (obs, old, novel) -> {
            if (novel == null) {
                clearOntologyTree();
            } else {
                initializeOntology(novel);
            }
        };
    }

    private void clearOntologyTree() {
        ontologyBrowserBox.setDisable(true);
        ontologyTreeView.setRoot(null);
        ontologyTreeView.setCellFactory(null);
//        phenotypeDescriptionInFocus.removeListener(termIdChangeListener);
    }

    private void initializeOntology(Ontology ontology) {
        ontologyBrowserBox.setDisable(false);
        ontologyTreeView.setRoot(simpleRoot(ontology));
        ontologyTreeView.setCellFactory(tv -> SimpleOntologyTreeCell.of());
//        phenotypeDescriptionInFocus.addListener(termIdChangeListener);
    }

    private void navigateToTermId(TermId termId) {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            // shouldn't happen but let's be 100% sure
            return;
        if (OntologyAlgorithm.existsPath(ontology, termId, ontology.getRootTermId())) {
            // find root -> term path through the tree
            Stack<TermId> stack = new Stack<>();
            stack.add(termId);
            Set<TermId> parents = ontology.getParentTermIds(termId); //getTermParents(term);
            while (parents.size() != 0) {
                TermId parent = parents.iterator().next();
                stack.add(ontology.getTermMap().get(parent).getId());
                parents = ontology.getParentTermIds(parent);
            }

            // expand tree nodes in top -> down direction
            List<TreeItem<SimpleOntologyTreeTerm>> children = ontologyTreeView.getRoot().getChildren();
            stack.pop(); // get rid of 'All' node which is hidden
            TreeItem<SimpleOntologyTreeTerm> target = ontologyTreeView.getRoot();
            while (!stack.empty()) {
                TermId current = stack.pop();
                for (TreeItem<SimpleOntologyTreeTerm> child : children) {
                    if (child.getValue().term().getId().equals(current)) {
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
}
