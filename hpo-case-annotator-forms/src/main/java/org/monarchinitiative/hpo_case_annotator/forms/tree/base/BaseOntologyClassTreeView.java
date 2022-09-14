package org.monarchinitiative.hpo_case_annotator.forms.tree.base;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public abstract class BaseOntologyClassTreeView<T extends OntologyClass> extends TreeView<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOntologyClassTreeView.class);

    protected final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>();

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    protected BaseOntologyClassTreeView() {
        setShowRoot(false);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        disableProperty().bind(ontology.isNull());
        ontology.addListener((obs, old, novel) -> {
            setRoot(null);
            bind(novel);
        });
        setCellFactory(prepareCellFactory());
    }


    public ReadOnlyObjectProperty<TreeItem<T>> selectedItemProperty() {
        return getSelectionModel().selectedItemProperty();
    }

    protected abstract Callback<TreeView<T>, TreeCell<T>> prepareCellFactory();

    protected abstract void bind(Ontology ontology);

    private static Stack<TermId> constructPath(Ontology ontology, TermId termId) {
        Stack<TermId> path = new Stack<>();
        path.add(termId);
        Set<TermId> parents = ontology.getParentTermIds(termId); //getTermParents(term);
        while (parents.size() != 0) {
            TermId parent = parents.iterator().next();
            path.add(ontology.getTermMap().get(parent).id());
            parents = ontology.getParentTermIds(parent);
        }
        return path;
    }

    public void scrollTo(OntologyClass ontologyClass) {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            // shouldn't happen but let's be 100% sure
            return;
        if (OntologyAlgorithm.existsPath(ontology, ontologyClass.getId(), ontology.getRootTermId())) {
            // find root -> term path through the tree
            Stack<TermId> stack = constructPath(ontology, ontologyClass.getId());

            // expand tree nodes in top -> down direction
            List<TreeItem<T>> children = getRoot().getChildren();
            stack.pop(); // get rid of 'All' node which is hidden
            TreeItem<T> target = getRoot();
            while (!stack.empty()) {
                TermId current = stack.pop();
                for (TreeItem<T> child : children) {
                    if (child.getValue().id().equals(current)) {
                        child.setExpanded(true);
                        target = child;
                        children = child.getChildren();
                        break;
                    }
                }
            }
            requestFocus();
            getSelectionModel().select(target);
            scrollTo(getSelectionModel().getSelectedIndex() - 5);
        } else {
            LOGGER.warn("Unable to find the path from {} to {}", ontology.getRootTermId(), ontologyClass);
        }
    }
}
