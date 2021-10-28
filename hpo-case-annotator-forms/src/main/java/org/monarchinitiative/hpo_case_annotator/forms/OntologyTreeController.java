package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.model.OntologyTreeTerm;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeCell;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeItem;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeTermSimple;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OntologyTreeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyTreeController.class);

    private final ObjectProperty<TermId> selectedTermId = new SimpleObjectProperty<>(this, "selectedTermId", null);

    private final Ontology ontology;
    private final Map<String, TermId> termNameToId;

    @FXML
    private TextField searchTextField;

    @FXML
    private TreeView<OntologyTreeTermSimple> ontologyTreeView;

    public OntologyTreeController(Ontology ontology) {
        this.ontology = ontology;
        this.termNameToId = new HashMap<>(ontology.getTermMap().size());
        ontology.getTermMap().values()
                .forEach(term -> termNameToId.putIfAbsent(term.getName(), term.getId()));
    }

    public void initialize() {
        initializeOntologyTree();
        initializeSearchTextField();
    }

    private void initializeOntologyTree() {
        ontologyTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (ontology != null) {
            TreeItem<OntologyTreeTermSimple> root = simpleRoot(ontology.getTermMap().get(ontology.getRootTermId()));
//            TreeItem<OntologyTreeTermSimple> root = generateTheEntireOntologyGraph(ontology);
            ontologyTreeView.setRoot(root);
            ontologyTreeView.setCellFactory(tv -> OntologyTreeCell.of());
        }
        // always focus on whatever TermId is in `selectedTermId`
        selectedTermId.addListener((obs, old, novel) -> {
            if (novel != null)
                navigateToTermId(novel);
        });
    }

    private TreeItem<OntologyTreeTermSimple> simpleRoot(Term term) {
        return OntologyTreeItem.of(ontology, term);
    }

    private TreeItem<OntologyTreeTermSimple> generateTheEntireOntologyGraph(Ontology ontology) {
        TermId rootTermId = ontology.getRootTermId();
        Map<TermId, Term> termMap = ontology.getTermMap();
        // takes arount .3621 seconds to create the entire tree
        Queue<TreeItem<OntologyTreeTermSimple>> queue = new LinkedList<>();
        TreeItem<OntologyTreeTermSimple> root = new TreeItem<>(OntologyTreeTermSimple.of(termMap.get(rootTermId)));
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeItem<OntologyTreeTermSimple> treeItem = queue.remove();
            Term term = treeItem.getValue().getTerm();
            TermId termId = term.getId();
            List<TreeItem<OntologyTreeTermSimple>> children = OntologyAlgorithm.getChildTerms(ontology, termId, false).stream()
                    .map(tid -> new TreeItem<>(OntologyTreeTermSimple.of(termMap.get(tid))))
                    .toList();
            treeItem.getChildren().addAll(children);
            queue.addAll(children);
        }

        return root;
    }

    private void initializeSearchTextField() {
        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(searchTextField, termNameToId.keySet());
        binding.prefWidthProperty().bind(searchTextField.widthProperty());
    }

    @FXML
    private void searchTextFieldAction(ActionEvent event) {
        String termName = searchTextField.getText();
        TermId id = termNameToId.get(termName);
        if (id == null) {
            // TODO: 10/27/21 alert the user that we cannot find this one
            return;
        }
        if (id.equals(selectedTermId.get()))
            selectedTermId.set(null);
        selectedTermId.setValue(id);
        searchTextField.clear();
        event.consume();
    }

    public List<OntologyTreeTerm> getSelectedTerms() {
        // TODO: 10/27/21 implement
        return List.of();
    }

    /**
     * Find the path from the root term to given {@link TermId}, expand the tree and set the selection model of the
     * TreeView to the term position.
     *
     * @param termId {@link TermId} to be displayed
     */
    private void navigateToTermId(TermId termId) {
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
            List<TreeItem<OntologyTreeTermSimple>> children = ontologyTreeView.getRoot().getChildren();
            stack.pop(); // get rid of 'All' node which is hidden
            TreeItem<OntologyTreeTermSimple> target = ontologyTreeView.getRoot();
            while (!stack.empty()) {
                TermId current = stack.pop();
                for (TreeItem<OntologyTreeTermSimple> child : children) {
                    if (child.getValue().getTerm().getId().equals(current)) {
                        child.setExpanded(true);
                        target = child;
                        children = child.getChildren();
                        break;
                    }
                }
            }
            ontologyTreeView.requestFocus();
            ontologyTreeView.getSelectionModel().select(target);
            ontologyTreeView.scrollTo(ontologyTreeView.getSelectionModel().getSelectedIndex());
        } else {
            LOGGER.warn("Unable to find the path from {} to {}", ontology.getRootTermId(), termId);
        }
    }
}
