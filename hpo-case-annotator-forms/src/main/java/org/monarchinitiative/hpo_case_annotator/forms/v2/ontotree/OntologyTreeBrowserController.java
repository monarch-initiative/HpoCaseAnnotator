package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
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

import java.util.List;
import java.util.Stack;

public class OntologyTreeBrowserController extends BaseOntologyTreeBrowser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyTreeBrowserController.class);
    private ObjectBinding<TermId> selectedTermId;
    @FXML
    private VBox ontologyBrowserBox;
    @FXML
    private TreeView<SimpleOntologyTreeTerm> ontologyTreeView;

    private static TreeItem<SimpleOntologyTreeTerm> simpleRoot(Ontology ontology) {
        Term root = ontology.getTermMap().get(ontology.getRootTermId());
        return SimpleOntologyTreeItem.of(ontology, root);
    }


    @FXML
    private void initialize() {
        ontologyTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ontology.addListener(getOntologyChangeListener());
        selectedTermId = Bindings.createObjectBinding(() -> {
            TreeItem<SimpleOntologyTreeTerm> item = ontologyTreeView.getSelectionModel().getSelectedItem();
            return (item == null)
                    ? null
                    : item.getValue().term().id();
        }, ontologyTreeView.getSelectionModel().selectedItemProperty());
    }

    public ChangeListener<ObservablePhenotypicFeature> phenotypeDescriptionChangeListener() {
        return (obs, old, novel) -> {
            if (novel != null)
                navigateToTermId(novel.getTermId());
        };
    }

    public TermId getSelectedTermId() {
        return selectedTermId.get();
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
    }

    private void initializeOntology(Ontology ontology) {
        ontologyBrowserBox.setDisable(false);
        ontologyTreeView.setRoot(simpleRoot(ontology));
        ontologyTreeView.setCellFactory(tv -> SimpleOntologyTreeCell.of());
    }

    public void navigateToTermId(TermId termId) {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            // shouldn't happen but let's be 100% sure
            return;
        if (OntologyAlgorithm.existsPath(ontology, termId, ontology.getRootTermId())) {
            // find root -> term path through the tree
            Stack<TermId> path = constructPath(ontology, termId);

            // expand tree nodes in top -> down direction
            List<TreeItem<SimpleOntologyTreeTerm>> children = ontologyTreeView.getRoot().getChildren();
            path.pop(); // get rid of 'All' node which is hidden
            TreeItem<SimpleOntologyTreeTerm> target = ontologyTreeView.getRoot();
            while (!path.empty()) {
                TermId current = path.pop();
                for (TreeItem<SimpleOntologyTreeTerm> child : children) {
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
}
