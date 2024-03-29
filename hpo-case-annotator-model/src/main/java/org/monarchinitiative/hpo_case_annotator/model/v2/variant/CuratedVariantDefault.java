package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

import java.util.Objects;
import java.util.Optional;

record CuratedVariantDefault(String genomicAssembly,
                             GenomicVariant variant,
                             VariantMetadata variantMetadata) implements CuratedVariant {

    CuratedVariantDefault(String genomicAssembly,
                          GenomicVariant variant,
                          VariantMetadata variantMetadata) {
        this.genomicAssembly = Objects.requireNonNull(genomicAssembly, "Genomic assembly must not be null");
        this.variant = Objects.requireNonNull(variant, "Variant must not be null");
        this.variantMetadata = Objects.requireNonNull(variantMetadata, "Variant metadata must not be null");
    }

    static CuratedVariantDefault of(String genomicAssembly,
                                    GenomicVariant variant,
                                    VariantMetadata variantMetadata) {
        return new CuratedVariantDefault(genomicAssembly, variant, variantMetadata);
    }

    @Override
    public String getGenomicAssembly() {
        return genomicAssembly;
    }

    @Override
    public Optional<GenomicVariant> getVariant() {
        // Never empty
        return Optional.of(variant);
    }

    @Override
    public VariantMetadata getVariantMetadata() {
        return variantMetadata;
    }

}
