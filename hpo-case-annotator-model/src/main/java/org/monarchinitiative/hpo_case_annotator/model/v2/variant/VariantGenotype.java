package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

/**
 * A tuple for mapping variant MD5 hash to a particular genotype in an individual.
 */
public interface VariantGenotype {

    static VariantGenotype of(String md5Hex, Genotype genotype) {
        return new VariantGenotypeDefault(md5Hex, genotype);
    }

    /**
     * @return get variant MD5 hash.
     */
    String getMd5Hex();

    /**
     *
     * @return get {@link Genotype} of the variant with given {@code md5Hex}.
     */
    Genotype getGenotype();

}
