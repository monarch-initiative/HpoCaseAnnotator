package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.*;

import java.util.Optional;

public class Convert {

    protected static final Strand VCF_STRAND = Strand.POSITIVE;
    protected static final CoordinateSystem VCF_COORDINATE_SYSTEM = CoordinateSystem.oneBased();

    private Convert() {
    }

    public static Optional<ObservableSeqSymVariant> toObservableSeqSymVariant(CuratedVariant curatedVariant) {
        Variant variant = curatedVariant.variant();
        if (variant instanceof BreakendVariant)
            return Optional.empty();

        ObservableSeqSymVariant osv = new ObservableSeqSymVariant();

        osv.setContig(variant.contig());
        osv.setId(variant.id());

        // Always use VCF strand and coordinate system here
        osv.setStart(variant.startOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM));
        osv.setEnd(variant.endOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM));

        osv.setChangeLength(variant.changeLength());
        osv.setRef(variant.ref());
        osv.setAlt(variant.alt());
        osv.setVariantType(variant.variantType());

        return Optional.of(osv);
    }

    public static CuratedVariant toCuratedVariant(ObservableSeqSymVariant variant) {

        // make sure you consider both variant type and alt sequence when converting to curated variant
        // TODO
        return null;
    }

    public static Optional<ObservableBreakendVariant> toObservableBreakendVariant(CuratedVariant curatedVariant) {
        if (!(curatedVariant.variant() instanceof BreakendVariant))
            return Optional.empty();
        BreakendVariant variant = (BreakendVariant) curatedVariant.variant();


        // TODO
        return Optional.empty();
    }

    public static CuratedVariant toCuratedVariant(ObservableBreakendVariant variant) {
        // TODO
        return null;
    }
}
