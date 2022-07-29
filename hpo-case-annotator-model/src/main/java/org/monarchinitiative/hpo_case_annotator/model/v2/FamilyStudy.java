package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;
import java.util.List;

public interface FamilyStudy extends Study {

    static FamilyStudy of(String id,
                          Publication publication,
                          List<CuratedVariant> variants,
                          Pedigree pedigree,
                          StudyMetadata studyMetadata) {
        return new FamilyStudyDefault(id, publication, variants, pedigree, studyMetadata);
    }

    Pedigree getPedigree();

    // Individual (Family)
    // - Map<variant_id, genotype>
    // - phenotype time course

    default List<? extends PedigreeMember> getMembers() {
        return getPedigree().getMembers();
    }

    // Variant(s)
    // possibly put to Study
    // - variant_id
    // - genome build
    // - svart bits
    // - gene
    //   - id
    //   - transcript


    // Phenotype(s)
    //
    default List<? extends PhenotypicFeature> phenotypicFeatures() {
        return getMembers().stream()
                .map(Individual::getPhenotypicFeatures)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    // Disease(s)
    default List<? extends DiseaseStatus> diseases() {
        return getMembers().stream()
                .map(Individual::getDiseaseStates)
                .flatMap(Collection::stream)
                .toList();
    }

}
