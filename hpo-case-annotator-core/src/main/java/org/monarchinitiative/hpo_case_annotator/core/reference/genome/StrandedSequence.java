package org.monarchinitiative.hpo_case_annotator.core.reference.genome;

import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.util.Seq;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Daniel Danis
 */
public class StrandedSequence extends BaseGenomicRegion<StrandedSequence> {

    private final String sequence;

    protected StrandedSequence(Contig contig, Strand strand, Coordinates coordinates, String sequence) {
        super(contig, strand, coordinates);
        this.sequence = sequence;
        if (length() != sequence.length()) {
            throw new IllegalArgumentException("Sequence length " + sequence.length() + " does not match length of the region " + length());
        }
    }

    public static StrandedSequence of(GenomicRegion region, String sequence) {
        return of(region.contig(), region.strand(), region.coordinates(), sequence);
    }

    public static StrandedSequence of(Contig contig, Strand strand, Coordinates coordinates, String sequence) {
        return new StrandedSequence(contig, strand, coordinates, sequence);
    }

    public static StrandedSequence of(Contig contig, Strand strand, CoordinateSystem coordinateSystem, int start, int end, String sequence) {
        return of(contig, strand, Coordinates.of(coordinateSystem, start, end), sequence);

    }

    public String sequence() {
        return sequence;
    }

    @Override
    protected StrandedSequence newRegionInstance(Contig contig, Strand strand, Coordinates coordinates) {
        return new StrandedSequence(contig, strand, coordinates, strand == strand() ? sequence : Seq.reverseComplement(sequence));
    }

    /**
     * @param query query contigPosition
     * @return string with sequence that corresponds to <code>query</code> contigPosition or <code>null</code> if at least one
     * base from the <code>contigPosition</code> is not available
     */
    public Optional<String> subsequence(final GenomicRegion query) {
        if (contains(query)) {
            GenomicRegion queryOnStrand = query.withStrand(strand()).toZeroBased();
            // when slicing a sequence, we always use 0-based coordinates - that's why we pre-calculate `start`
            // field in the constructor
            String seq = sequence.substring(
                    queryOnStrand.start() - startWithCoordinateSystem(CoordinateSystem.zeroBased()),
                    queryOnStrand.end() - startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            return Optional.of(query.strand() == strand()
                    ? seq
                    : Seq.reverseComplement(seq));
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StrandedSequence that = (StrandedSequence) o;
        return Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sequence);
    }

    @Override
    public String toString() {
        return "StrandedSequence{" +
                "sequence='" + sequence + '\'' +
                "} " + super.toString();
    }
}
