package org.monarchinitiative.hpo_case_annotator.test;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class TestData {

    public static class V1 {
        public static DiseaseCase comprehensiveCase() {
            return org.monarchinitiative.hpo_case_annotator.test.V1.comprehensiveCase();
        }
    }

    public static class V2 {
        public static FamilyStudy comprehensiveFamilyStudy() {
            return org.monarchinitiative.hpo_case_annotator.test.V2.comprehensiveFamilyStudy();
        }

        public static CohortStudy comprehensiveCohortStudy() {
            return org.monarchinitiative.hpo_case_annotator.test.V2.comprehensiveCohortStudy();
        }
    }


}
