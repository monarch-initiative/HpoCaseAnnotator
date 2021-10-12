package org.monarchinitiative.hpo_case_annotator.export;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.export.test_resources.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CodecsTest {

    @Test
    public void getPhenopacketIdFor_aguilar_Ramirez_2009_C5() {
        DiseaseCase dc = TestResources.aguilar_Ramirez_2009_C5();

        String id = Codecs.getPhenopacketIdFor(dc);
        assertThat(id, equalTo("Aguilar_Ramirez-2009-19375167-C5-1_II:9"));
    }

    @Test
    public void getPhenopacketIdFor_structural_beygo_2012_TCOF1_M18662() {
        DiseaseCase dc = TestResources.structural_beygo_2012_TCOF1_M18662();

        String id = Codecs.getPhenopacketIdFor(dc);
        assertThat(id, equalTo("Beygo-2012-22712005-TCOF1-M18662"));
    }
}