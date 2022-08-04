package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PedigreeMember extends Individual {

    static PedigreeMember of(String id,
                             String paternalId,
                             String maternalId,
                             boolean isProband,
                             Collection<? extends PhenotypicFeature> phenotypicFeatures,
                             List<? extends DiseaseStatus> diseases,
                             List<? extends VariantGenotype> genotypes,
                             TimeElement age,
                             Sex sex) {
        return PedigreeMemberDefault.of(id,
                paternalId,
                maternalId,
                isProband,
                phenotypicFeatures,
                diseases,
                genotypes,
                age,
                sex);
    }

    Optional<String> getPaternalId();

    Optional<String> getMaternalId();

    boolean isProband();

    int hashCode();

    boolean equals(Object o);

}
