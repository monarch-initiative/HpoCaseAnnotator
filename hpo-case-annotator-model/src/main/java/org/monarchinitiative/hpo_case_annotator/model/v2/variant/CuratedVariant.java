package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.apache.commons.codec.digest.DigestUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

public interface CuratedVariant extends Variant {

    static CuratedVariant sequenceSymbolic(Contig contig,
                                           String id,
                                           Strand strand,
                                           Coordinates coordinates,
                                           String ref,
                                           String alt,
                                           int changeLength,
                                           VariantMetadata variantMetadata) {
        return SequenceSymbolicCuratedVariant.of(contig, id, strand, coordinates, ref, alt, changeLength, variantMetadata);
    }

    static CuratedVariant breakend(String eventId,
                                   Breakend left,
                                   Breakend right,
                                   String ref,
                                   String alt,
                                   VariantMetadata variantMetadata) {
        return BreakendCuratedVariant.of(eventId, left, right, ref, alt, variantMetadata);
    }

    VariantMetadata metadata();

    default String md5Hex() {
        String id;
        if (this instanceof BreakendVariant bv) {
            Breakend left = bv.left();
            Breakend right = bv.right();
            id = String.join("-",
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
                    contigName(),
                    String.valueOf(startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    String.valueOf(endWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    ref(),
                    alt());
        }
        return DigestUtils.md5Hex(id);
    }

    int hashCode();

    boolean equals(Object o);

}
