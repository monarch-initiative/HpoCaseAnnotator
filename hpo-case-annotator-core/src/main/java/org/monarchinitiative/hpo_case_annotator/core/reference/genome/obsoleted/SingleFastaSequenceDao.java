package org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * This class allows to fetch arbitrary nucleotide sequence from the reference genome. To do so it requires single
 * Fasta file that contains all contigs. Fasta index (*.fai) is required to be present in the same directory. The
 * index can be created using command <code>samtools faidx file.fa</code> from the <code>samtools</code> suite.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public final class SingleFastaSequenceDao implements SequenceDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFastaSequenceDao.class);

    private final IndexedFastaSequenceFile fasta;

    private final File fastaPath;


    /**
     * Create an instance using FASTA file located at <code>fastaPath</code>.
     *
     * @param fastaPath path to FASTA file. FASTA index is expected to be in the same directory with the same
     *                  basename as the FASTA file + ".fai" suffix
     */
    public SingleFastaSequenceDao(File fastaPath) {
        this(fastaPath, new File(fastaPath.getAbsolutePath() + ".fai"));
    }


    /**
     * Create an instance using provided FASTA file and index.
     *
     * @param fastaPath path to indexed FASTA file
     * @param indexPath path to FASTA index
     */
    public SingleFastaSequenceDao(File fastaPath, File indexPath) {
        this.fastaPath = fastaPath;
        LOGGER.debug("Opening indexed fasta file: {}, index: {}", fastaPath.getAbsolutePath(), indexPath.getAbsolutePath());
        FastaSequenceIndex fastaIndex = new FastaSequenceIndex(indexPath);
        fasta = new IndexedFastaSequenceFile(fastaPath, fastaIndex);
    }


    /**
     * Get sequence of nucleotides from given position specified by chromosome/contig name, starting position and ending
     * position. Case of nucleotides is not changed.
     * <p>
     * Querying with negative coordinates does not raise an exception, querying with e.g.
     * <code>fetchSequence("chr8", -6, -1)</code> returns ">chr8". However it does have any sense to do it.
     *
     * @param chr   chromosome name, prefix 'chr' will be included if not present
     * @param start start position using 0-based numbering (exclusive)
     * @param end   end chromosomal position using 0-based numbering (inclusive)
     * @return nucleotide sequence
     * @throws SAMException if the query asks for nucleotides past end of contig
     */
    @Override
    public String fetchSequence(String chr, int start, int end) throws SAMException {
        // Fasta from UCSC has prefix chr, hence we need to have it here from now on
        String chrom = (chr.startsWith("chr")) ? chr : "chr" + chr;

        ReferenceSequence referenceSequence = fasta.getSubsequenceAt(chrom, start + 1, end);
        return new String(referenceSequence.getBases());
    }

//    /**
//     * Extract nucleotide sequence from reference genome fasta file that is lying inside given {@link GenomeInterval}.
//     *
//     * @param interval where the nucleotide sequence will be extracted from.
//     *
//     * @return nucleotide sequence
//     */
//    @Override
//    public String fetchSequence(GenomeInterval interval) {
//        String chrom = interval.getRefDict().getContigIDToName().get(interval.getChr());
//        int begin, end;
//        switch (interval.getStrand()) {
//            case FWD:
//                begin = interval.getBeginPos() + 1; // convert to 1-based pos
//                end = interval.getEndPos();
//                return fetchSequence(chrom, begin, end);
//            case REV:
//                GenomeInterval fwd = new GenomeInterval(interval, Strand.FWD);
//                begin = fwd.getBeginPos() + 1;
//                end = fwd.getEndPos();
//                return reverseComplement(fetchSequence(chrom, begin, end));
//            default:
//                throw new IllegalArgumentException(String.format("Unknown strand %s", interval.getStrand()));
//        }
//    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        LOGGER.debug("Closing fasta file {}", fastaPath.getAbsolutePath());
        this.fasta.close();
    }
}
