package org.monarchinitiative.hpo_case_annotator.io.genome;

import htsjdk.samtools.SAMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * These tests test that we are able to retrieve sequence from an arbitrary genomic region. FASTA file that is used for
 * testing contains truncated chromosomes from UCSC genome build hg19. The file contains the first 1000 lines (~49 950 nt)
 * of all chromosomes.
 */
public class SingleFastaSequenceDaoTest {

    private static final File SINGLE_FASTA_PATH = new File(SingleFastaSequenceDaoTest.class.getResource("shortHg19.fa").getPath());

    private SingleFastaSequenceDao sequenceDao;



    @Before
    public void setUp() throws Exception {
        sequenceDao = new SingleFastaSequenceDao(SINGLE_FASTA_PATH);
    }


    @After
    public void tearDown() throws Exception {
        sequenceDao.close();
    }


    /**
     * Test retrieval of selected regions from chr8.
     */
    @Test
    public void testFetchSequencesFromChr8() {
        // the first line of chr8 in the test file
        assertThat(sequenceDao.fetchSequence("chr8", 0, 50), is("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"));
        // random retrieval
        assertThat(sequenceDao.fetchSequence("chr8", 39140, 39150), is("CTCGAGCCCT"));
        // the last line of chr8 in the test file
        assertThat(sequenceDao.fetchSequence("chr8", 49900, 49950), is("attttaggcagatagagaggaaaagaggtccttgggaagtttttgtttat"));
    }


    @Test
    public void testFetchSequencesFromVariousChromosomes() {
        assertThat(sequenceDao.fetchSequence("chr7", 44580, 44620), is("AGAGGAGGAAACGTGAATAGTATGCAGCTTCCCGCACACA"));
        assertThat(sequenceDao.fetchSequence("chrX", 42240, 42280), is("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"));
        assertThat(sequenceDao.fetchSequence("chrY", 9180, 9230), is("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"));
        assertThat(sequenceDao.fetchSequence("chrM", 11370, 11420), is("TAGTAAAGATACCTCTTTACGGACTCCACTTATGACTCCCTAAAGCCCAT"));
        assertThat(sequenceDao.fetchSequence("chr17_gl000206_random", 580, 630), is("cagatgctgatggggtgctggtgagcagtgcacacgcttgtcaaaactca"));
        assertThat(sequenceDao.fetchSequence("chr4_gl000194_random", 1980, 2030), is("GGGAGGTGGGCCCTAGCTGGAGGCACTGCACAGCAGCATCCTGGGTAGAG"));
    }


    /**
     * Test that an exception is thrown, if retrieving sequence beyond end of the truncated chromosome.
     */
    @Test(expected = SAMException.class)
    public void testFetchSingleNucleotideAfterEndOfContig() {
        // This query asks for one nucleotide past end of contig
        sequenceDao.fetchSequence("chr8", 49950, 49951);
    }


    /**
     * Test that an exception is thrown, if retrieving sequence beyond end of the truncated chromosome.
     */
    @Test(expected = SAMException.class)
    public void testFetchQueryReachingPastEndOfContig() {
        sequenceDao.fetchSequence("chr8", 49900, 49951);
    }


    /**
     * Querying with negative coordinates is somehow legal behaviour.
     */
    @Test
    public void testFetchOutsideOfBoundsBefore() {
        // This query asks for one nucleotide before begin of contig
        assertThat(sequenceDao.fetchSequence("chr8", -6, -1), is(">chr8"));
    }


    /**
     * This test should result in an exception: <code>htsjdk.samtools.SAMException: Unable to find entry for contig: chrZ</code>
     */
    @Test(expected = SAMException.class)
    public void testNonExistingContig() {
        sequenceDao.fetchSequence("chrZ", 50, 100);
    }
}