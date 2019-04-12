package org.monarchinitiative.hpo_case_annotator.model.test_resources;

import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.phenopackets.schema.v1.Family;
import org.phenopackets.schema.v1.Phenopacket;

import java.net.URL;

/**
 * This class exposes data (e.g. {@link Phenopacket}s, {@link DiseaseCase}s) suitable for testing the
 * <code>hpo-case-annotator-model</code> classes.
 */
public final class TestResources {

    public static final URL TEST_XML_MODEL_FILE_DIR = XMLModelParser.class.getResource("");

    public static final URL TEST_GPI_MODEL_FILE_DIR = TestResources.class.getResource("/gpi_model");


    private TestResources() {
        // private no-op
    }

    /**
     * Utility method for returning a {@link Phenopacket} rare disease example based on the case defined in
     * src/test/resources/toronto_rare_disease_example.md
     *
     * @return a {@link Phenopacket} containing a rare disease patient and relations.
     */
    public static Family rareDiseaseFamily() {
        return RareDiseaseFamilyExample.rareDiseaseFamily();
    }

    /**
     * @return {@link Phenopacket} consistent with content of file <em>examplePhenoPacket_v0.3.0.json</em>
     */
    public static Phenopacket mockPhenopacket() {
        return PhenopacketExample.mockPhenopacket();
    }

    public static DiseaseCase benMahmoud2013B3GLCT() {
        return DiseaseCaseModelExample.benMahmoud2013B3GLCT();
    }

    public static DiseaseCase v2Aznarez2003CFTR() {
        return DiseaseCaseModelExample.v2Aznarez2003CFTR();
    }

    public static DiseaseCase aguilar_Ramirez_2009_C5() {
        return DiseaseCaseModelExample.aguilar_Ramirez_2009_C5();
    }
}
