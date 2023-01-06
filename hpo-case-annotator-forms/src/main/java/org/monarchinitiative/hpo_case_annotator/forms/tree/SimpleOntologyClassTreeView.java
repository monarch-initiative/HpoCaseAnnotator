package org.monarchinitiative.hpo_case_annotator.forms.tree;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.forms.tree.base.BaseOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.forms.tree.simple.OntologyClassTreeCell;
import org.monarchinitiative.hpo_case_annotator.forms.tree.simple.OntologyClassTreeItem;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

public class SimpleOntologyClassTreeView extends BaseOntologyClassTreeView<SimpleOntologyClass> {

    @Override
    protected Callback<TreeView<SimpleOntologyClass>, TreeCell<SimpleOntologyClass>> prepareCellFactory() {
        return tw -> new OntologyClassTreeCell();
    }

    @Override
    protected void bind(Ontology ontology) {
        if (ontology != null) {
            // bind
            Term root = ontology.getTermMap().get(ontology.getRootTermId());
            if (root != null) {
                SimpleOntologyClass pf = new SimpleOntologyClass(root.id(), root.getName());
                setRoot(new OntologyClassTreeItem(ontology, pf));
            } else
                setRoot(null);
        }
    }

}
