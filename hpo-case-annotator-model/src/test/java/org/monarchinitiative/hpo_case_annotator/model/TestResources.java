package org.monarchinitiative.hpo_case_annotator.model;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.PhenoPacket;

import java.net.URL;

/**
 * This class exposes data (e.g. {@link PhenoPacket}s, {@link DiseaseCase}s) suitable for testing the
 * <code>hpo-case-annotator-model</code> classes.
 */
public class TestResources {

    public static final URL TEST_XML_MODEL_FILE_DIR = TestResources.class.getResource("/models/xml");

    public static final URL TEST_GPI_MODEL_FILE_DIR = TestResources.class.getResource("/gpi_model");


    private TestResources() {
        // private no-op
    }

    /**
     * Utility method for returning a {@link PhenoPacket} rare disease example based on the case defined in
     * src/test/resources/toronto_rare_disease_example.md
     *
     * @return a {@link PhenoPacket} containing a rare disease patient and relations.
     */
    public static PhenoPacket rareDiseasePhenoPacket() {
        return RareDiseasePhenoPacketExample.rareDiseasePhenoPacket();
    }


    public static DiseaseCase benMahmoud2013B3GLCT() {
        return DiseaseCaseModelExample.benMahmoud2013B3GLCT();
    }
}
