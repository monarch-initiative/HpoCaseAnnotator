package org.monarchinitiative.hpo_case_annotator.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Classes
 */
public interface ModelExporter {

    void exportModels(String filePath) throws IOException;

    void exportModels(File filePath) throws IOException;

    void exportModels(Writer writer);
}
