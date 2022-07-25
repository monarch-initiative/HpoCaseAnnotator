package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.util.MD5Digest;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

public interface CuratedVariant {

    static CuratedVariant of(String genomicAssembly,
                             GenomicVariant variant,
                             VariantMetadata variantMetadata) {
        return CuratedVariantDefault.of(genomicAssembly, variant, variantMetadata);
    }

    String getGenomicAssembly();

    GenomicVariant getVariant();

    VariantMetadata getVariantMetadata();

    default String md5Hex() {
        String id;
        GenomicVariant variant = getVariant();
        if (variant instanceof GenomicBreakendVariant bv) {
            GenomicBreakend left = bv.left();
            GenomicBreakend right = bv.right();
            id = String.join("-",
                    getGenomicAssembly(),
                    left.contigName(),
                    // For breakends, start == end in 0-based CS, no need to hash both.
                    String.valueOf(left.startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    right.contigName(),
                    String.valueOf(right.startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    bv.eventId(),
                    bv.ref(),
                    bv.alt());
        } else {
            id = String.join("-",
                    getGenomicAssembly(),
                    variant.contig().name(),
                    String.valueOf(variant.startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    String.valueOf(variant.endWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    variant.ref(),
                    variant.alt());
        }
        return MD5Digest.digest(id);
    }

    // ************************************** DERIVED METHODS ******************************************************** *

    default String id() {
        if (getVariant() instanceof GenomicBreakendVariant bv) {
            return bv.eventId();
        } else {
            return getVariant().id();
        }
    }

    default int changeLength() {
        return getVariant().changeLength();
    }

    default VariantType variantType() {
        return getVariant().variantType();
    }

    // -----------------------------------------------------------------------------------------------------------------

    int hashCode();

    boolean equals(Object o);

}
