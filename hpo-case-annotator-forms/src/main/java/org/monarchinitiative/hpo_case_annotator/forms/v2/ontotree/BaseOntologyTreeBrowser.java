package org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Set;
import java.util.Stack;

public class BaseOntologyTreeBrowser {

    protected final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    protected static Stack<TermId> constructPath(Ontology ontology, TermId termId) {
        Stack<TermId> path = new Stack<>();
        path.add(termId);
        Set<TermId> parents = ontology.getParentTermIds(termId); //getTermParents(term);
        while (parents.size() != 0) {
            TermId parent = parents.iterator().next();
            path.add(ontology.getTermMap().get(parent).getId());
            parents = ontology.getParentTermIds(parent);
        }
        return path;
    }
}
