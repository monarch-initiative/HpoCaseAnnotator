package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface FamilyStudy extends Study {

    static FamilyStudy of(Publication publication,
                          List<CuratedVariant> variants,
                          Pedigree pedigree,
                          StudyMetadata studyMetadata) {
        return new FamilyStudyDefault(publication, variants, pedigree, studyMetadata);
    }

    Pedigree pedigree();

    // Individual (Family)
    // - Map<variant_id, genotype>
    // - phenotype time course

    default Stream<? extends PedigreeMember> members() {
        return pedigree().members();
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
    default List<PhenotypicFeature> phenotypicFeatures() {
        return members()
                .map(Individual::phenotypicObservations)
                .flatMap(Collection::stream)
                .map(PhenotypicObservation::phenotypicFeatures)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    // Disease(s)
    default List<Disease> diseases() {
        return members()
                .map(Individual::diseases)
                .flatMap(Collection::stream)
                .toList();
    }

}
