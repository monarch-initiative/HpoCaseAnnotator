package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;

/**
 * A study of two or more unrelated individuals.
 */
public interface CohortStudy extends GroupStudy<Individual> {

    static CohortStudy of(String id,
                          Publication publication,
                          Collection<CuratedVariant> variants,
                          Collection<? extends Individual> members,
                          StudyMetadata studyMetadata) {
        return CohortStudyDefault.of(id, publication, variants, members, studyMetadata);
    }

}
