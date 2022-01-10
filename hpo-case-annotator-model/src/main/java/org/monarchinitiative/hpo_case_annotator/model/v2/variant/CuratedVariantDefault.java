package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

import java.util.Objects;

record CuratedVariantDefault(String genomicAssembly,
                             Variant variant,
                             VariantMetadata variantMetadata) implements CuratedVariant {

    CuratedVariantDefault(String genomicAssembly,
                          Variant variant,
                          VariantMetadata variantMetadata) {
        this.genomicAssembly = Objects.requireNonNull(genomicAssembly, "Genomic assembly must not be null");
        this.variant = Objects.requireNonNull(variant, "Variant must not be null");
        this.variantMetadata = Objects.requireNonNull(variantMetadata, "Variant metadata must not be null");
    }

    static CuratedVariantDefault of(String genomicAssembly,
                                    Variant variant,
                                    VariantMetadata variantMetadata) {
        return new CuratedVariantDefault(genomicAssembly, variant, variantMetadata);
    }

}
