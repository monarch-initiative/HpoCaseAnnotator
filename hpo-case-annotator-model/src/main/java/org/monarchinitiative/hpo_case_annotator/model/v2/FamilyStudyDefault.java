package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

record FamilyStudyDefault(Publication publication,
                          List<CuratedVariant> variants,
                          Pedigree pedigree,
                          StudyMetadata studyMetadata) implements FamilyStudy {
}
