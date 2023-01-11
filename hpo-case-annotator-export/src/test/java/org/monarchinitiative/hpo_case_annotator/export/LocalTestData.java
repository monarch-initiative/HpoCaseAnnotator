package org.monarchinitiative.hpo_case_annotator.export;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.nio.file.Path;

public class LocalTestData {

    private static final Path TEST_DATA_BASE = Path.of("src/test/resources/org/monarchinitiative/hpo_case_annotator/export");

    private static final Ontology HPO = loadHpo();

    private static Ontology loadHpo() {
        return OntologyLoader.loadOntology(TEST_DATA_BASE.resolve("hp.toy.json").toFile());
    }

    private LocalTestData() {}
    
    public static Ontology getHpo() {
        return HPO;
    }
    
    
}
