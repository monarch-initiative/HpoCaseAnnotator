package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.apache.commons.codec.digest.DigestUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

public interface CuratedVariant {

    static CuratedVariant of(String genomicAssembly,
                             Variant variant,
                             VariantMetadata variantMetadata) {
        return CuratedVariantDefault.of(genomicAssembly, variant, variantMetadata);
    }

    String genomicAssembly();

    Variant variant();

    VariantMetadata variantMetadata();

    default String md5Hex() {
        String id;
        if (variant() instanceof BreakendVariant bv) {
            Breakend left = bv.left();
            Breakend right = bv.right();
            id = String.join("-",
                    genomicAssembly(),
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
                    genomicAssembly(),
                    contig().name(),
                    String.valueOf(startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    String.valueOf(endWithCoordinateSystem(CoordinateSystem.zeroBased())),
                    ref(),
                    alt());
        }
        return DigestUtils.md5Hex(id);
    }

    default Contig contig() {
        return variant().contig();
    }

    default Strand strand() {
        return variant().strand();
    }

    default Coordinates coordinates() {
        return variant().coordinates();
    }

    default int start() {
        return coordinates().start();
    }

    default int startWithCoordinateSystem(CoordinateSystem target) {
        return variant().startWithCoordinateSystem(target);
    }

    default int startOnStrandWithCoordinateSystem(Strand strand, CoordinateSystem coordinateSystem) {
        return variant().startOnStrandWithCoordinateSystem(strand, coordinateSystem);
    }

    default int end() {
        return coordinates().end();
    }

    default int endWithCoordinateSystem(CoordinateSystem target) {
        return variant().endWithCoordinateSystem(target);
    }

    default int endOnStrandWithCoordinateSystem(Strand strand, CoordinateSystem coordinateSystem) {
        return variant().endOnStrandWithCoordinateSystem(strand, coordinateSystem);
    }

    default String ref() {
        return variant().ref();
    }

    default String alt() {
        return variant().alt();
    }

    default String id() {
        if (variant() instanceof BreakendVariant bv) {
            return bv.eventId();
        } else {
            return variant().id();
        }
    }

    default int changeLength() {
        return variant().changeLength();
    }

    default VariantType variantType() {
        return variant().variantType();
    }

    // -----------------------------------------------------------------------------------------------------------------

    int hashCode();

    boolean equals(Object o);

}
