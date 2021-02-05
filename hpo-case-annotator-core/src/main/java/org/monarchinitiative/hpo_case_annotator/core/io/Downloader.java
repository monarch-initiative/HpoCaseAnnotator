package org.monarchinitiative.hpo_case_annotator.core.io;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Downloader extends Task<Void> {

    private static final Logger log = LoggerFactory.getLogger(Downloader.class);

    /**
     * This is the absolute path to the place where downloaded file will be
     * saved in local filesystem.
     */
    private final File whereToSave;

    /**
     * This is the URL of the file we want to download.
     */
    private final URL url;


    /**
     * Download resource from given {@link URL} and save it to specified location on local filesystem.
     *
     * @param url         - where to download the resource from.
     * @param whereToSave - where to save the resource on local filesystem.
     */
    public Downloader(URL url, File whereToSave) {
        this.url = url;
        this.whereToSave = whereToSave;
    }


    /**
     * This method downloads a file to the specified local file path. If the file already exists, it emits a warning
     * message and does nothing.
     */
    @Override
    protected Void call() throws Exception {
        if (!whereToSave.getParentFile().exists()) {
            if (!whereToSave.getParentFile().mkdirs()) { // try to create the directory
                String msg = String.format("Unable to create directory %s. Aborting the download.", whereToSave.getParent());
                log.warn(msg);
                throw new IOException(msg);
            }
        }

        String msg = String.format("Downloading: %s", url);
        log.info(msg);
        updateMessage(msg);
        URLConnection connection = url.openConnection();
        try (FileOutputStream writer = new FileOutputStream(whereToSave);
             InputStream reader = connection.getInputStream()) {
            byte[] buffer = new byte[153600];
            int totalBytesRead = 0;
            int bytesRead;
            int fileLength = connection.getContentLength();
            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[153600];
                totalBytesRead += bytesRead;
                updateProgress(totalBytesRead, fileLength);
            }
            log.info(String.format("Done, %d bytes read.", totalBytesRead));
        } catch (MalformedURLException mue) {
            log.error(String.format("Could not interpret url: %s", url), mue);
        } catch (IOException ioe) {
            log.error(String.format("IO Exception reading from URL: %s", url), ioe);
        }
        if (isCancelled()) {
            whereToSave.delete();
        }
        updateMessage("Finished!");
        updateProgress(1, 1);
        return null;
    }
}
