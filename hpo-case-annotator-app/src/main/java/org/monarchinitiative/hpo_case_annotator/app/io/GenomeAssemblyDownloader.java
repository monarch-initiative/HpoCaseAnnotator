package org.monarchinitiative.hpo_case_annotator.app.io;

import htsjdk.samtools.reference.FastaSequenceIndex;
import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.util.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

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

        LOGGER.info("Downloading genomic assembly report from `{}`", genomeAssemblyReportUrl);
        downloadResource(genomeAssemblyReportUrl, genomicAssemblyPaths.getAssemblyReport());
        LOGGER.info("Downloading reference genome from `{}`", genomeUrl);
        downloadResource(genomeUrl, genomeTarGz.toPath());

        LOGGER.info("Concatenating reference genome into a single file at `{}`", genomicAssemblyPaths.getFasta());
        concatenateIntoSingleMulticontigFasta(genomeTarGz, genomicAssemblyPaths.getFasta());

        LOGGER.info("Indexing FASTA file `{}`", genomicAssemblyPaths.getFasta());
        updateMessage("Indexing FASTA file");
        updateProgress(-1, 1);
        FastaSequenceIndex index = Genome.indexFastaFile(genomicAssemblyPaths.getFasta(), genomicAssemblyPaths.getFastaFai());
        updateProgress(1, 1);

        LOGGER.info("Building SAM sequence dictionary");
        updateMessage("Building SAM sequence dictionary");
        updateProgress(-1, 1);
        Genome.buildSamSequenceDictionary(index, genomicAssemblyPaths.getFastaDict());
        updateProgress(1, 1);
        updateMessage("Done");

        return null;
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