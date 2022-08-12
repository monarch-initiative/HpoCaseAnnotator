package org.monarchinitiative.hpo_case_annotator.forms.ontotree;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeView;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class SelectablePhenotypeFeatureTreeView extends TreeView<PhenotypeFeatureItem> implements Observable {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>();
    private final PhenotypeFeatureTreeItemManager phenotypeFeatureTreeItemManager = new PhenotypeFeatureTreeItemManager();

    public SelectablePhenotypeFeatureTreeView() {
        //noinspection ConstantConditions
        getStylesheets().add(PhenotypeFeatureTreeItem.class.getResource("onto-tree-view.css").toExternalForm());
        setShowRoot(false);
        disableProperty().bind(ontology.isNull());
        setCellFactory(tw -> new PhenotypeFeatureTreeCell());

        ontology.addListener(initializeTreeView());
    }

    private ChangeListener<Ontology> initializeTreeView() {
        return (obs, old, novel) -> {
            if (old != null) {
                // unbind
                setRoot(null);
            }
            if (novel != null) {
                // bind
                Term root = novel.getTermMap().get(novel.getRootTermId());
                if (root != null) {
                    setRoot(new PhenotypeFeatureTreeItem(phenotypeFeatureTreeItemManager.getOntoItem(root), novel, phenotypeFeatureTreeItemManager));
                } else
                    setRoot(null);
            }
        };
    }

    public Ontology getOntology() {
        return ontology.get();
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        phenotypeFeatureTreeItemManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        phenotypeFeatureTreeItemManager.removeListener(listener);
    }

    public MapProperty<TermId, PhenotypeFeatureItem> ontoItemsProperty() {
        return phenotypeFeatureTreeItemManager.ontoItemsProperty();
    }

}
