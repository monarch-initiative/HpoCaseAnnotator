package org.monarchinitiative.hpo_case_annotator.model;

import org.phenopackets.schema.v1.PhenoPacket;

import java.net.URL;

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
}
