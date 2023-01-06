package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

record FamilyStudyDefault(String id,
                          Publication publication,
                          List<CuratedVariant> variants,
                          Pedigree pedigree,
                          StudyMetadata studyMetadata) implements FamilyStudy {
    @Override
    public Pedigree getPedigree() {
        return pedigree;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Publication getPublication() {
        return publication;
    }

    @Override
    public List<? extends CuratedVariant> getVariants() {
        return variants;
    }

    @Override
    public StudyMetadata getStudyMetadata() {
        return studyMetadata;
    }
}
