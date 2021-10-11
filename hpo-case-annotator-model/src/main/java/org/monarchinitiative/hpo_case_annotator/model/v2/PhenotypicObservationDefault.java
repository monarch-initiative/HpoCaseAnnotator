package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Set;

record PhenotypicObservationDefault(AgeRange observationAge,
                                    Set<PhenotypicFeature> phenotypicFeatures) implements PhenotypicObservation {
}
