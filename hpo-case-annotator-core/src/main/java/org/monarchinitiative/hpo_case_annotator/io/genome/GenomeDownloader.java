package org.monarchinitiative.hpo_case_annotator.io.genome;

import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;

/**
 * This class downloads big
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public final class GenomeDownloader extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenomeDownloader.class);

    private static final int BUFFER_SIZE = 81920;

    private final URL genomeUrl;

    private final File whereToSave;

    private boolean updateTaskVariables = true;


    public GenomeDownloader(URL genomeUrl, File whereToSave) {
        this.genomeUrl = genomeUrl;
        this.whereToSave = whereToSave;
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
    protected Void call() throws Exception {
        // download genome tar.gz archive into a temporary location
        File genomeTarGz = File.createTempFile("hca-genome-downloader", ".tar.gz");
        genomeTarGz.deleteOnExit();
        download(genomeTarGz);

        // concatenate all the files in the tar.gz archive into a single FASTA file
        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(genomeTarGz)));
             OutputStream os = new FileOutputStream(whereToSave)) {

            LOGGER.debug("Concatenating chromosomes from {} into a single FASTA file {}",
                    genomeTarGz.getAbsolutePath(), whereToSave.getAbsolutePath());
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

        // create fasta index for the FASTA file
        LOGGER.debug("Indexing FASTA file {}", whereToSave.getAbsolutePath());
        if (updateTaskVariables) {
            updateProgress(-1, 1);
            updateMessage("Indexing FASTA file " + whereToSave.getAbsolutePath());
        }
        FastaSequenceIndexCreator.create(Paths.get(whereToSave.toURI()), true);
        return null;
    }


    /**
     * Download a file from {@link URL} to given location.
     *
     * @param whereToSave {@link File} path where the file will be downloaded
     * @throws IOException if error occurs
     */
    private void download(File whereToSave) throws IOException {
        if (updateTaskVariables)
            updateMessage("Downloading reference genome files from " + genomeUrl.toExternalForm());
        LOGGER.debug("Downloading reference genome files from " + genomeUrl.toExternalForm());

        URLConnection connection = genomeUrl.openConnection();
        try (FileOutputStream writer = new FileOutputStream(whereToSave);
             InputStream reader = connection.getInputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long contentSize = connection.getContentLengthLong();
            int bytesRead, totalRead = 0;
            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (updateTaskVariables)
                    updateProgress(totalRead, contentSize);
            }
            if (updateTaskVariables)
                updateProgress(1, 1);
        }
    }
}