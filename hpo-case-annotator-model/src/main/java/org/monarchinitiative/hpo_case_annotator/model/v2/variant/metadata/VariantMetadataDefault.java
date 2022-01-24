package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

class VariantMetadataDefault extends VariantMetadata {

    protected VariantMetadataDefault(VariantMetadataContext variantMetadataContext, String snippet, String variantClass, String pathomechanism, boolean cosegregation, boolean comparability) {
        super(variantMetadataContext, snippet, variantClass, pathomechanism, cosegregation, comparability);
    }

    @Override
    public String toString() {
        return "VariantMetadataDefault{} " + super.toString();
    }
}
