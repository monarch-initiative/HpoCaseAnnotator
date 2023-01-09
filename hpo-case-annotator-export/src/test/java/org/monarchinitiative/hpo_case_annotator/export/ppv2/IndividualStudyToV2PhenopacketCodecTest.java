package org.monarchinitiative.hpo_case_annotator.export.ppv2;

import com.google.protobuf.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.export.LocalTestData;
import org.monarchinitiative.hpo_case_annotator.model.v2.IndividualStudy;
import org.monarchinitiative.hpo_case_annotator.test.TestData;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.phenopackets.schema.v2.Phenopacket;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;

public class IndividualStudyToV2PhenopacketCodecTest {

    private static final Ontology HPO = LocalTestData.getHpo();

    private IndividualStudyToV2PhenopacketCodec codec;

    @BeforeEach
    public void setUp() {
        codec = new IndividualStudyToV2PhenopacketCodec(HPO);
    }

    @Test
    public void convertIndividualStudy() throws Exception {
        IndividualStudy individualStudy = TestData.V2.comprehensiveIndividualStudy();

        Message message = codec.encode(individualStudy);

        assertThat(message, is(instanceOf(Phenopacket.class)));
//        System.err.println(message);

        // TODO(ielis) - more tests
    }

//    @Test
//    @Disabled // not yet implemented
//    public void convertFamilyStudy() throws Exception {
//        FamilyStudy familyStudy = TestData.V2.comprehensiveFamilyStudy();
//
//        Message message = codec.encode(familyStudy);
//
//        assertThat(message, is(instanceOf(Family.class)));
//        System.err.println(message); // TODO(ielis) - more tests
//    }
//
//    @Test
//    @Disabled // not yet implemented
//    public void convertCohortStudy() throws Exception {
//        CohortStudy cohortStudy = TestData.V2.comprehensiveCohortStudy();
//
//        Message message = codec.encode(cohortStudy);
//
//        assertThat(message, is(instanceOf(Cohort.class)));
//        System.err.println(message);// TODO(ielis) - more tests
//    }
}