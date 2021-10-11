package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.Set;

public interface PhenotypicObservation {

    static PhenotypicObservation of(AgeRange observationAge, Set<PhenotypicFeature> phenotypicFeatures) {
        return new PhenotypicObservationDefault(observationAge, Set.copyOf(phenotypicFeatures));
    }

    AgeRange observationAge();

    Set<PhenotypicFeature> phenotypicFeatures();

    int hashCode();

    boolean equals(Object o);
}
