package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.phenol.ontology.data.Ontology;

public class Resources {

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

}
