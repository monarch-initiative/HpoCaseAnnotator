package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.ModelsForTesting;
import org.phenopackets.schema.v1.PhenoPacket;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


public class PhenopacketCodecTest {

    @Test
    public void diseaseCaseToPhenopacket() {
        DiseaseCase diseaseCase = ModelsForTesting.benMahmoud2013B3GLCT();

        final PhenoPacket packet = PhenopacketCodec.diseaseCaseToPhenopacket(diseaseCase);
        // TODO - implement functionality
        assertThat(packet, is(notNullValue()));
    }

    @Test
    public void phenopacketToDiseaseCase() {
        PhenoPacket packet = TestResources.rareDiseasePhenoPacket();
        final DiseaseCase diseaseCase = PhenopacketCodec.phenopacketToDiseaseCase(packet);
        // TODO - implement functionality
        assertThat(diseaseCase, is(notNullValue()));
    }
}