package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PedigreeMember extends Individual {

    static PedigreeMember of(String id,
                             String paternalId,
                             String maternalId,
                             boolean isProband,
                             Collection<PhenotypicFeature> phenotypicFeatures,
                             List<DiseaseStatus> diseases,
                             Map<String, Genotype> genotypes,
                             Period age,
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

    Optional<String> paternalId();

    Optional<String> maternalId();

    boolean isProband();

    int hashCode();

    boolean equals(Object o);

}
