package org.monarchinitiative.hpo_case_annotator.export;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.test.TestData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UtilsTest {

    @Test
    public void getPhenopacketIdFor_aguilar_Ramirez_2009_C5() {
        DiseaseCase dc = TestData.V1.comprehensiveCase();

        String id = Utils.getPhenopacketIdFor(dc);
        assertThat(id, equalTo("Beygo-2012-22712005-CFTR-FAM:001"));
    }

}