package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;

public interface CohortStudy extends Study {

    static CohortStudy of(String id,
                          Publication publication,
                          Collection<CuratedVariant> variants,
                          Collection<? extends Individual> members,
                          StudyMetadata studyMetadata) {
        return CohortStudyDefault.of(id, publication, variants, members, studyMetadata);
    }

}
