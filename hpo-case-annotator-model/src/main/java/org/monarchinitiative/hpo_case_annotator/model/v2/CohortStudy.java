package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CohortStudy extends Study {

    static CohortStudy of(Publication publication,
                          List<CuratedVariant> variants,
                          Collection<? extends Individual> individuals,
                          StudyMetadata studyMetadata) {
        return new CohortStudyDefault(publication,
                variants,
                Set.copyOf(individuals),
                studyMetadata);
    }

    Set<Individual> individuals();

}
