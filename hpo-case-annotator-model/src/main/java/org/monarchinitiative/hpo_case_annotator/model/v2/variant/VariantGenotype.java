package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

/**
 * A tuple for mapping variant ID to a particular genotype in an individual.
 */
public interface VariantGenotype {

    static VariantGenotype of(String id, Genotype genotype) {
        return new VariantGenotypeDefault(id, genotype);
    }

    /**
     * @return get variant ID.
     */
    String getId();

    /**
     *
     * @return get {@link Genotype} of the variant with given {@code id}.
     */
    Genotype getGenotype();

}
