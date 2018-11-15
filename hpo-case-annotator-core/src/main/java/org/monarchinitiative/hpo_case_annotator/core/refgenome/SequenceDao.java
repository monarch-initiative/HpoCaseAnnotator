package org.monarchinitiative.hpo_case_annotator.core.refgenome;

/**
 * This interface is implemented by classes that fetch nucleotide sequences from specific genomic regions.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public interface SequenceDao extends AutoCloseable {

    /**
     * Get String of nucleotide sequence present on given genomic coordinates (FWD strand).
     *
     * @param chromosome {@link String} denoting chromosome/contig
     * @param begin      {@link Integer} denoting the first base of the sequence on FWD strand of chromosome using
     *                   1-based (including) coordinates
     * @param end        {@link Integer} denoting the last base of the sequence on FWD strand of chromosome using
     *                   1-based (including) coordinates
     * @return {@link String} containing the nucleotide sequence
     */
    String fetchSequence(String chromosome, int begin, int end);

//    /**
//     * Get nucleotide sequence in String format from given genomic interval respecting the strand.
//     *
//     * @param interval {@link GenomeInterval} representing the genomic interval
//     * @return {@link String} containing the nucleotide sequence
//     */
//    String fetchSequence(GenomeInterval interval);

    /**
     * Convert nucleotide sequence to reverse complement.
     *
     * @param sequence of nucleotides, only {a,c,g,t,n,A,C,G,T,N} permitted.
     * @return reverse complement of given sequence
     */
    default String reverseComplement(String sequence) {
        char[] oldSeq = sequence.toCharArray();
        char[] newSeq = new char[oldSeq.length];
        int idx = oldSeq.length - 1;
        for (int i = 0; i < oldSeq.length; i++) {
            if (oldSeq[i] == 'A') {
                newSeq[idx - i] = 'T';
            } else if (oldSeq[i] == 'a') {
                newSeq[idx - i] = 't';
            } else if (oldSeq[i] == 'T') {
                newSeq[idx - i] = 'A';
            } else if (oldSeq[i] == 't') {
                newSeq[idx - i] = 'a';
            } else if (oldSeq[i] == 'C') {
                newSeq[idx - i] = 'G';
            } else if (oldSeq[i] == 'c') {
                newSeq[idx - i] = 'g';
            } else if (oldSeq[i] == 'G') {
                newSeq[idx - i] = 'C';
            } else if (oldSeq[i] == 'g') {
                newSeq[idx - i] = 'c';
            } else if (oldSeq[i] == 'N') {
                newSeq[idx - i] = 'N';
            } else if (oldSeq[i] == 'n') {
                newSeq[idx - i] = 'n';
            } else throw new IllegalArgumentException(String.format("Illegal nucleotide %s in sequence %s",
                    oldSeq[i], sequence));
        }
        return new String(newSeq);
    }
}
