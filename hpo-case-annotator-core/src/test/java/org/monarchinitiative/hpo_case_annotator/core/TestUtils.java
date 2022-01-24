package org.monarchinitiative.hpo_case_annotator.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class TestUtils {

    private TestUtils() {
    }

    public static String readFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
