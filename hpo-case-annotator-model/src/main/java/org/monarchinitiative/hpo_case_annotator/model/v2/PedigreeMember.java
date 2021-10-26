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
                             Period age,
                             List<DiseaseStatus> diseases,
                             Map<String, Genotype> genotypes,
                             Collection<PhenotypicFeature> phenotypicFeatures,
                             boolean isProband,
                             Sex sex) {
        return PedigreeMemberDefault.of(id,
                paternalId,
                maternalId,
                age,
                diseases,
                genotypes,
                phenotypicFeatures,
                isProband,
                sex);
    }

    Optional<String> paternalId();

    Optional<String> maternalId();

    boolean isProband();

    int hashCode();

    boolean equals(Object o);

}
