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
        String id = String.join("-",
                contigName(),
                String.valueOf(startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.zeroBased())),
                String.valueOf(endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.zeroBased())),
                ref(),
                alt());
        return DigestUtils.md5Hex(id);
    }

    int hashCode();

    boolean equals(Object o);

}
