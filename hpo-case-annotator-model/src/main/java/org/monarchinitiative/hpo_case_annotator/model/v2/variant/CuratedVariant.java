package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.util.MD5Digest;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.*;

import java.util.Optional;
import java.util.OptionalInt;

public interface CuratedVariant {

    CoordinateSystem VCF_COORDINATE_SYSTEM = CoordinateSystem.oneBased();
    Strand VCF_STRAND = Strand.POSITIVE;

    static CuratedVariant of(String genomicAssembly,
                             GenomicVariant variant,
                             VariantMetadata variantMetadata) {
        return CuratedVariantDefault.of(genomicAssembly, variant, variantMetadata);
    }

    String getGenomicAssembly();

    /**
     * Get {@link GenomicVariant} if all bits for its assembly are present.
     * Note that {@link GenomicBreakendVariant} is returned if the variant is a translocation.
     *
     * @return optional with variant or an empty optional if the data is incomplete.
     */
    Optional<GenomicVariant> getVariant();

    VariantMetadata getVariantMetadata();

    default String md5Hex() {
        String id;
        Optional<GenomicVariant> variant = getVariant();
        if (variant.isPresent()) {
            GenomicVariant v = variant.get();
            if (v instanceof GenomicBreakendVariant bv) {
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
                        v.contig().name(),
                        String.valueOf(v.startWithCoordinateSystem(CoordinateSystem.zeroBased())),
                        String.valueOf(v.endWithCoordinateSystem(CoordinateSystem.zeroBased())),
                        v.ref(),
                        v.alt());
            }
        } else {
            id = "";
        }

        return MD5Digest.digest(id);
    }

    // ************************************** DERIVED METHODS ******************************************************** *

    default Optional<String> id() {
        return getVariant().map(v -> {
            if (v instanceof GenomicBreakendVariant bv) {
                return bv.eventId();
            } else {
                return v.id();
            }
        });
    }

    default OptionalInt changeLength() {
        Optional<GenomicVariant> variant = getVariant();
        return variant.map(genomicVariant -> OptionalInt.of(genomicVariant.changeLength()))
                .orElseGet(OptionalInt::empty);
    }

    default Optional<VariantType> variantType() {
        return getVariant().map(GenomicVariant::variantType);
    }

}
