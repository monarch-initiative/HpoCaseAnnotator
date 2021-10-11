package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.BaseBreakendVariant;
import org.monarchinitiative.svart.Breakend;

import java.util.Objects;

class BreakendCuratedVariant extends BaseBreakendVariant<BreakendCuratedVariant> implements CuratedVariant {

    private final VariantMetadata variantMetadata;

    private BreakendCuratedVariant(String eventId,
                                   Breakend left,
                                   Breakend right,
                                   String ref,
                                   String alt,
                                   VariantMetadata variantMetadata) {
        super(eventId, left, right, ref, alt);
        this.variantMetadata = variantMetadata;
    }

    static BreakendCuratedVariant of(String eventId,
                                     Breakend left,
                                     Breakend right,
                                     String ref,
                                     String alt,
                                     VariantMetadata variantMetadata) {
        return new BreakendCuratedVariant(eventId,
                left,
                right,
                ref,
                alt,
                variantMetadata);
    }

    @Override
    public VariantMetadata metadata() {
        return variantMetadata;
    }

    @Override
    protected BreakendCuratedVariant newBreakendVariantInstance(String eventId, Breakend left, Breakend right, String ref, String alt) {
        return new BreakendCuratedVariant(eventId, left, right, ref, alt, variantMetadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BreakendCuratedVariant that = (BreakendCuratedVariant) o;
        return Objects.equals(variantMetadata, that.variantMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variantMetadata);
    }

    @Override
    public String toString() {
        return "BreakendCuratedVariant{" +
                "variantMetadata=" + variantMetadata +
                "} " + super.toString();
    }
}
