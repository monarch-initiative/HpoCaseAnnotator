package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

record VariantGenotypeDefault(String id, Genotype genotype) implements VariantGenotype {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public Genotype getGenotype() {
        return genotype;
    }
}
