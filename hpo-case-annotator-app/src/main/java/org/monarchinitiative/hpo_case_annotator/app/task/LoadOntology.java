package org.monarchinitiative.hpo_case_annotator.app.task;

import javafx.concurrent.Task;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class LoadOntology extends Task<Ontology> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadOntology.class);

    private final Path ontologyPath;

    public LoadOntology(Path ontologyPath) {
        this.ontologyPath = ontologyPath;
    }

    @Override
    protected Ontology call() throws Exception {
        LOGGER.debug("Loading ontology from {}", ontologyPath.toAbsolutePath());
        return OntologyLoader.loadOntology(ontologyPath.toFile());
    }
}
