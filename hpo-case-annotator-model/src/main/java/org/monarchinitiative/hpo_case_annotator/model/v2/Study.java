package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.List;
import java.util.stream.Stream;

/**
 * An investigation described in a single publication.
 */
public interface Study {

    String id();

    Publication publication();

    List<CuratedVariant> variants();

    Stream<? extends Individual> members();

    StudyMetadata studyMetadata();

}
