package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

class VariantMetadataDefault extends VariantMetadata {

    static final VariantMetadataDefault EMPTY = new VariantMetadataDefault(VariantMetadataContext.UNKNOWN,
            null,
            "N/A",
            "N/A",
            false,
            false);

    VariantMetadataDefault(VariantMetadataContext variantMetadataContext,
                           String snippet,
                           String variantClass,
                           String pathomechanism,
                           boolean cosegregation,
                           boolean comparability) {
        super(variantMetadataContext,
                snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability);
    }

    @Override
    public String toString() {
        return "VariantMetadataDefault{} " + super.toString();
    }
}
