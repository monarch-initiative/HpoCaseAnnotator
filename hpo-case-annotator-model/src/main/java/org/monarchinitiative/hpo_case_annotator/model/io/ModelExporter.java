package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.*;
import java.util.Collection;

/**
 * Classes
 */
public interface ModelExporter {

    void exportModels(Collection<DiseaseCase> cases, Writer writer);


    default void exportModels(Collection<DiseaseCase> cases, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            exportModels(cases, writer);
        }
    }


    default void exportModels(Collection<DiseaseCase> cases, File filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            exportModels(cases, writer);
        }
    }
}
