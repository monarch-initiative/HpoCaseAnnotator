package org.monarchinitiative.hpo_case_annotator.app.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloads {

    private static final int BUFFER_SIZE = 8 * 1024;

    private Downloads() {
    }

    public static void download(URL url, Path target) throws IOException {
        URLConnection connection = url.openConnection();
        try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(target));
             InputStream reader = connection.getInputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

}
