package org.monarchinitiative.hpo_case_annotator.app.io;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceDictionaryCodec;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.FastaSequenceIndexEntry;
import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class downloads big
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class GenomeAssemblyDownloader extends Task<Void> {

//    TODO - create generic downloader located in GUI module

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomeAssemblyDownloader.class);

    private static final int BUFFER_SIZE = 81920;

    private final URL genomeUrl;
    private final URL genomeAssemblyReportUrl;

    private final GenomicLocalResource genomicAssemblyPaths;

    private boolean updateTaskVariables = true;


    public GenomeAssemblyDownloader(URL genomeUrl,
                                    URL genomeAssemblyReportUrl,
                                    GenomicLocalResource genomicAssemblyPaths) {
        this.genomeUrl = genomeUrl;
        this.genomeAssemblyReportUrl = genomeAssemblyReportUrl;
        this.genomicAssemblyPaths = genomicAssemblyPaths;
    }


    /**
     * @param updateTaskVariables set to <code>false</code> if the downloader is not being used in JavaFX program.
     *                            Otherwise, an exception will be thrown, since JavaFX toolkit is not initalized.
     *                            <code>true</code> by default.
     */
    public void setUpdateTaskVariables(boolean updateTaskVariables) {
        this.updateTaskVariables = updateTaskVariables;
    }


    /**
     * Download the genome tar.gz file, concatenate all chromosomes into a single gzipped file, index the file.
     */
    @Override
    public Void call() throws Exception {
        // download genome tar.gz archive into a temporary location
        File genomeTarGz = File.createTempFile("hca-genome-downloader", ".tar.gz");
        genomeTarGz.deleteOnExit();

        downloadResource(genomeAssemblyReportUrl, genomicAssemblyPaths.getAssemblyReport());
        downloadResource(genomeUrl, genomeTarGz.toPath());

        concatenateIntoSingleMulticontigFasta(genomeTarGz, genomicAssemblyPaths.getFasta());

        FastaSequenceIndex index = indexFastaFile(genomicAssemblyPaths.getFasta(), genomicAssemblyPaths.getFastaFai());

        buildSequenceDictionary(index, genomicAssemblyPaths.getFastaDict());

        return null;
    }

    private static void buildSequenceDictionary(FastaSequenceIndex index, Path fastaDict) throws IOException {
        List<SAMSequenceRecord> records = new ArrayList<>(index.size());
        for (FastaSequenceIndexEntry entry : index) {
            records.add(new SAMSequenceRecord(entry.getContig(), Math.toIntExact(entry.getSize())));
        }

        SAMSequenceDictionary sequenceDictionary = new SAMSequenceDictionary(records);
        LOGGER.info("Writing FASTA sequence dictionary to `{}`", fastaDict);
        try (BufferedWriter writer = Files.newBufferedWriter(fastaDict)) {
            SAMSequenceDictionaryCodec codec = new SAMSequenceDictionaryCodec(writer);
            codec.encode(sequenceDictionary);
        }
    }

    private void concatenateIntoSingleMulticontigFasta(File genomeTarGz, Path fastaPath) throws IOException {
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(genomeTarGz)));
             OutputStream os = new BufferedOutputStream(Files.newOutputStream(fastaPath))) {

            LOGGER.debug("Concatenating chromosomes from {} into a single FASTA file {}", genomeTarGz.getAbsolutePath(), fastaPath);
            TarArchiveEntry tarEntry = tarInput.getNextTarEntry();

            while (tarEntry != null) {
                LOGGER.debug("Appending chromosome {}", tarEntry.getName());
                if (updateTaskVariables) {
                    updateProgress(-1, 1);
                    updateMessage("Appending chromosome " + tarEntry.getName());
                }
                IOUtils.copy(tarInput, os);
                tarEntry = tarInput.getNextTarEntry();
            }
        }
    }

    private FastaSequenceIndex indexFastaFile(Path fastaPath, Path indexPath) throws IOException {
        LOGGER.debug("Indexing FASTA file {}", fastaPath);
        if (updateTaskVariables) {
            updateProgress(-1, 1);
            updateMessage("Indexing FASTA file " + fastaPath);
        }
        FastaSequenceIndex index = FastaSequenceIndexCreator.buildFromFasta(fastaPath);
        index.write(indexPath);
        if (updateTaskVariables) {
            updateProgress(1, 1);
            updateMessage("Done!");
        }
        return index;
    }


    /**
     * Download a file from {@link URL} to given location.
     *
     * @param url resource URL
     * @param target {@link Path} path where the file will be downloaded
     * @throws IOException if error occurs
     */
    private void downloadResource(URL url, Path target) throws IOException {
        if (updateTaskVariables)
            updateMessage("Downloading from " + url.toExternalForm());
        LOGGER.debug("Downloading from " + url.toExternalForm());

        URLConnection connection = url.openConnection();
        try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(target));
             InputStream reader = connection.getInputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long contentSize = connection.getContentLengthLong();
            int bytesRead, totalRead = 0;
            while ((bytesRead = reader.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (updateTaskVariables)
                    updateProgress(totalRead, contentSize);
            }
            if (updateTaskVariables)
                updateProgress(1, 1);
        }
    }
}