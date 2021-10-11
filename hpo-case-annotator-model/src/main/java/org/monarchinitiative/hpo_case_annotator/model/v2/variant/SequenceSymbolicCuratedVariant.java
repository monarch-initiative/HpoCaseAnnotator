package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.BaseVariant;
import org.monarchinitiative.svart.Contig;
import org.monarchinitiative.svart.Coordinates;
import org.monarchinitiative.svart.Strand;

import java.util.Objects;

class SequenceSymbolicCuratedVariant extends BaseVariant<SequenceSymbolicCuratedVariant> implements CuratedVariant {

    private final VariantMetadata variantMetadata;

    private SequenceSymbolicCuratedVariant(Contig contig,
                                           String id,
                                           Strand strand,
                                           Coordinates coordinates,
                                           String ref,
                                           String alt,
                                           int changeLength,
                                           VariantMetadata variantMetadata) {
        super(contig, id, strand, coordinates, ref, alt, changeLength);
        this.variantMetadata = variantMetadata;
    }

    static SequenceSymbolicCuratedVariant of(Contig contig,
                                             String id,
                                             Strand strand,
                                             Coordinates coordinates,
                                             String ref,
                                             String alt,
                                             int changeLength,
                                             VariantMetadata variantMetadata) {
        return new SequenceSymbolicCuratedVariant(contig, id, strand, coordinates, ref, alt, changeLength, variantMetadata);
    }

    @Override
    public VariantMetadata metadata() {
        return variantMetadata;
    }

    @Override
    protected SequenceSymbolicCuratedVariant newVariantInstance(Contig contig, String id, Strand strand, Coordinates coordinates, String ref, String alt, int changeLength) {
        return new SequenceSymbolicCuratedVariant(contig, id, strand, coordinates, ref, alt, changeLength, variantMetadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SequenceSymbolicCuratedVariant that = (SequenceSymbolicCuratedVariant) o;
        return Objects.equals(variantMetadata, that.variantMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variantMetadata);
    }

    @Override
    public String toString() {
        return "SequenceSymbolicCuratedVariant{" +
                "variantMetadata=" + variantMetadata +
                "} " + super.toString();
    }
}
