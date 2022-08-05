package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

record VariantGenotypeDefault(String md5Hex, Genotype genotype) implements VariantGenotype {
    @Override
    public String getMd5Hex() {
        return md5Hex;
    }

    @Override
    public Genotype getGenotype() {
        return genotype;
    }
}
