package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

record IndividualStudyDefault(String id,
                              Publication publication,
                              List<CuratedVariant> variants,
                              Individual individual,
                              StudyMetadata studyMetadata) implements IndividualStudy {

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Individual getIndividual() {
        return individual;
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
