package org.monarchinitiative.hpo_case_annotator.model.io;

import java.io.*;

/**
 * Classes
 */
public interface ModelExporter {

    void exportModels(Writer writer);

    default void exportModels(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            exportModels(writer);
        }
    }

    default void exportModels(File filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            exportModels(writer);
        }
    }
}
