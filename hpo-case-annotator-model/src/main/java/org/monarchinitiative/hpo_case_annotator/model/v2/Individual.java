package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Individual {

    static Individual of(String id,
                         Collection<PhenotypicFeature> phenotypicFeatures,
                         List<DiseaseStatus> diseases,
                         Map<String, Genotype> genotypes,
                         Period age,
                         Sex sex) {
        return IndividualDefault.of(id,
                phenotypicFeatures,
                diseases,
                genotypes,
                age,
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

    Stream<PhenotypicFeature> phenotypicFeatures();

    List<DiseaseStatus> diseases();

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
