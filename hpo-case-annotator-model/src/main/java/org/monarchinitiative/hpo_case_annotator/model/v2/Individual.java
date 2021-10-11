package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Individual {

    static Individual of(String id,
                         Period age,
                         List<PhenotypicObservation> phenotypicObservations,
                         List<Disease> diseases,
                         Map<String, Genotype> genotypes,
                         Sex sex) {
        return IndividualDefault.of(id,
                age,
                phenotypicObservations,
                diseases,
                genotypes,
                sex);
    }

    /**
     * @return String with ID of the individual within the publication.
     */
    String id();

    /**
     * @return proband age at the time of the report.
     */
    Optional<Period> age();

    List<PhenotypicObservation> phenotypicObservations();

    List<Disease> diseases();

    /**
     * @return map linking {@link CuratedVariant#md5Hex()} to the {@link Genotype} observed in this individual.
     */
    Map<String, Genotype> genotypes();

    Sex sex();

    int hashCode();

    boolean equals(Object o);

    // ---------------------------------------------------------------------

    default boolean isAffected() {
        return diseases().stream()
                .anyMatch(d -> !d.isExcluded());
    }

}