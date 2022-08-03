package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Individual {

    static Individual of(String id,
                         List<PhenotypicFeature> phenotypicFeatures,
                         List<DiseaseStatus> diseases,
                         Map<String, Genotype> genotypes,
                         TimeElement age,
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
    String getId();

    /**
     * @return proband age at the time of the report or {@code null} if not known.
     */
    TimeElement getAge();

    List<? extends PhenotypicFeature> getPhenotypicFeatures();

    List<? extends DiseaseStatus> getDiseaseStates();

    /**
     * @return map linking {@link CuratedVariant#md5Hex()} to the {@link Genotype} observed in this individual.
     */
    Map<String, Genotype> getGenotypes();

    Sex getSex();

    int hashCode();

    boolean equals(Object o);

    // ---------------------------------------------------------------------

    /**
     * @return {@code true} if at least one disease is indicated to be present in the {@link Individual}.
     */
    default boolean isAffected() {
        return getDiseaseStates().stream()
                .anyMatch(DiseaseStatus::isPresent);
    }

}
