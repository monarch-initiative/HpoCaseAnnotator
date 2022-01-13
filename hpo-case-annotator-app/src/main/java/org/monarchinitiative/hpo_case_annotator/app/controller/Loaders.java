package org.monarchinitiative.hpo_case_annotator.app.controller;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.nio.file.Path;

public class Loaders {

    private Loaders() {
    }

    public static Ontology loadOntology(Path path) {
        try {
            return OntologyLoader.loadOntology(path.toFile());
        } catch (Exception e) {
            Dialogs.showException("Error", "Unable to load ontology\n" + path.toAbsolutePath(), e.getMessage(), e);
            return null;
        }
    }

}
