package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;

/**
 * An investigation described in a single publication.
 */
public interface Study {

    String getId();

    Publication getPublication();

    List<? extends CuratedVariant> getVariants();

    List<? extends Individual> getMembers();

    StudyMetadata getStudyMetadata();

}
