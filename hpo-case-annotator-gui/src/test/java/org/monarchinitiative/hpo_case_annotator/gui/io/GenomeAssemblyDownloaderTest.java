package org.monarchinitiative.hpo_case_annotator.gui.io;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test download of the ref genome tar.gz file, concatenation of all contigs into a single FASTA file and then indexing
 * using HTSJDK.
 */
public class GenomeAssemblyDownloaderTest {

    /**
     * URL pointing to tar.gz archive with fasta files containing first 1000 lines for each chromosome.
     */
    private static final URL GENOME_URL = GenomeAssemblyDownloaderTest.class.getResource("shortHg19ChromFa.tar.gz");

    private static final File BASE_DIR = new File(GenomeAssemblyDownloaderTest.class.getResource("").getFile());

    private static final File SINGLE_FASTA = new File(BASE_DIR, "gdt-shortHg19.fa");

    private static final File SINGLE_FASTA_FAI = new File(BASE_DIR, "gdt-shortHg19.fa.fai");

    private GenomeAssemblyDownloader downloader;


    @BeforeEach
    public void setUp() throws Exception {
        downloader = new GenomeAssemblyDownloader(GENOME_URL, SINGLE_FASTA);
        downloader.setUpdateTaskVariables(false);
    }


    @AfterEach
    public void tearDown() throws Exception {
        // these files are about to be created in the next test run, so delete them after successful testing
        if (SINGLE_FASTA.exists())
            SINGLE_FASTA.delete();

        if (SINGLE_FASTA_FAI.exists())
            SINGLE_FASTA_FAI.delete();
    }


    @Test
    public void testFunctionality() throws Exception {
        downloader.call();
        // files should exist after the downloader.call()
        assertTrue(SINGLE_FASTA.isFile());
        assertTrue(SINGLE_FASTA_FAI.isFile());

        IndexedFastaSequenceFile fasta = new IndexedFastaSequenceFile(SINGLE_FASTA);
        // there are 93 contigs in this single fasta file
        assertThat(fasta.getIndex().size(), is(93));

        assertThat(new String(fasta.getSubsequenceAt("chr7", 44581, 44620).getBases()), is("AGAGGAGGAAACGTGAATAGTATGCAGCTTCCCGCACACA"));
    }
}