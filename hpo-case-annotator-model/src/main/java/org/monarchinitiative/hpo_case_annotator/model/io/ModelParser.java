package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.*;
import java.util.Collection;

public interface ModelParser {

    /**
     * Save the model data using provided output stream.
     *
     * @param outputStream instance where the data will be written
     * @param model        {@link DiseaseCase} with data to be saved.
     */
    void saveModel(OutputStream outputStream, DiseaseCase model) throws IOException;

    /**
     * Read model instance with specified name from resource location. Return empty optional in case of I/O error or
     * absence of model.
     *
     * @param inputStream with model data
     * @return {@link DiseaseCase} containing read data
     * @throws FileNotFoundException if
     */
    DiseaseCase readModel(InputStream inputStream) throws IOException;

    /**
     * Return a collection with names of model files that are present in the model files directory.
     *
     * @return {@link Collection} of Strings of model filenames (with suffixes)
     */
    Collection<File> getModelNames();
}
