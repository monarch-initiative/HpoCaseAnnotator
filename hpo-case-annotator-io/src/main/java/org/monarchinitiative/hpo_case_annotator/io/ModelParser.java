package org.monarchinitiative.hpo_case_annotator.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public interface ModelParser<T> {

    void write(T model, OutputStream outputStream) throws IOException;

    default void write(T model, Path destination) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(destination)) {
            write(model, outputStream);
        }
    }

    T read(InputStream inputStream) throws IOException;

    default T read(Path model) throws IOException {
        try (InputStream inputStream = Files.newInputStream(model)) {
            return read(inputStream);
        }
    }

    default T read(String payload) throws IOException {
        return read(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));
    }

    @Deprecated
    Collection<File> getModelNames();
}
