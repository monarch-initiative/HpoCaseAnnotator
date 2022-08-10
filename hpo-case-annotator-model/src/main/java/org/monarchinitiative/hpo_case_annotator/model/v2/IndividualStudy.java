package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

/**
 * A study involving one individual, or a case report.
 */
public interface IndividualStudy extends Study {

    static IndividualStudy of(String id,
                          Publication publication,
                          List<CuratedVariant> variants,
                          Individual individual,
                          StudyMetadata studyMetadata) {
        return new IndividualStudyDefault(id, publication, variants, individual,studyMetadata);
    }

    Individual getIndividual();

}
