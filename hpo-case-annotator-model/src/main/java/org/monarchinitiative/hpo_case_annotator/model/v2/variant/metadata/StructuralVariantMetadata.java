package org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata;

public class StructuralVariantMetadata extends VariantMetadata {

    private StructuralVariantMetadata(String snippet,
                                      String variantClass,
                                      String pathomechanism,
                                      boolean cosegregation,
                                      boolean comparability) {
        super(VariantMetadataContext.STRUCTURAL,
                snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability);
    }

    public static StructuralVariantMetadata of(String snippet,
                                               String variantClass,
                                               String pathomechanism,
                                               boolean cosegregation,
                                               boolean comparability) {
        return new StructuralVariantMetadata(snippet,
                variantClass,
                pathomechanism,
                cosegregation,
                comparability);
    }

    @Override
    public String toString() {
        return "StructuralVariantMetadata{} " + super.toString();
    }
}
