package org.monarchinitiative.hpo_case_annotator.forms.tree;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.forms.tree.vettable.OntologyClassTreeCell;
import org.monarchinitiative.hpo_case_annotator.forms.tree.vettable.OntologyClassTreeItem;
import org.monarchinitiative.hpo_case_annotator.forms.tree.vettable.OntologyClassTreeItemManager;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;


public class VettableOntologyClassTreeView extends BaseOntologyClassTreeView<VettableOntologyClass> implements Observable {

    private final OntologyClassTreeItemManager phenotypeFeatureTreeItemManager = new OntologyClassTreeItemManager();

    public VettableOntologyClassTreeView() {
        //noinspection ConstantConditions
        getStylesheets().add(OntologyClassTreeItem.class.getResource("onto-tree-view.css").toExternalForm());
    }

    @Override
    protected Callback<TreeView<VettableOntologyClass>, TreeCell<VettableOntologyClass>> prepareCellFactory() {
        return tw -> new OntologyClassTreeCell();
    }

    @Override
    protected void bind(Ontology ontology) {
        if (ontology != null) {
            // bind
            Term root = ontology.getTermMap().get(ontology.getRootTermId());
            if (root != null) {
                setRoot(new OntologyClassTreeItem(phenotypeFeatureTreeItemManager.getOntologyClassForTerm(root), ontology, phenotypeFeatureTreeItemManager));
            } else
                setRoot(null);
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        phenotypeFeatureTreeItemManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        phenotypeFeatureTreeItemManager.removeListener(listener);
    }

    public MapProperty<TermId, VettableOntologyClass> ontoItemsProperty() {
        return phenotypeFeatureTreeItemManager.ontologyClassesProperty();
    }

}
