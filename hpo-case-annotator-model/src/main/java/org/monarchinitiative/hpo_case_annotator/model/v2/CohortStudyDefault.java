package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;
import java.util.Set;

record CohortStudyDefault(Publication publication,
                          List<CuratedVariant> variants,
                          Set<Individual> individuals,
                          StudyMetadata studyMetadata) implements CohortStudy {
}
