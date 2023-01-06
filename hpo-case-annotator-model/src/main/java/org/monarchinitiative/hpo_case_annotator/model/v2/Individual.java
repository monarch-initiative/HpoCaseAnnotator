package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.List;

public interface Individual {

    static Individual of(String id,
                         List<PhenotypicFeature> phenotypicFeatures,
                         List<DiseaseStatus> diseases,
                         List<VariantGenotype> genotypes,
                         TimeElement age,
                         VitalStatus vitalStatus,
                         Sex sex) {
        return IndividualDefault.of(id,
                phenotypicFeatures,
                diseases,
                genotypes,
                age,
                vitalStatus,
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

    /**
     * @return proband's vital status or {@code null} if not known.
     */
    VitalStatus getVitalStatus();

    Sex getSex();

    List<? extends PhenotypicFeature> getPhenotypicFeatures();

    List<? extends DiseaseStatus> getDiseaseStates();

    /**
     * @return list linking {@link CuratedVariant#md5Hex()} to the {@link Genotype} observed in this individual.
     */
    List<? extends VariantGenotype> getGenotypes();

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
