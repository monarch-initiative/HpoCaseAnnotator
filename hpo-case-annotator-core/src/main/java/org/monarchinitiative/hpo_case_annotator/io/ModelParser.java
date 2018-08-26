package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.io.*;
import java.util.Collection;

public interface ModelParser {

    /**
     * Save the model data using provided output stream.
     *
     * @param outputStream instance where the data will be written
     * @param model  {@link DiseaseCaseModel} with data to be saved.
     */
    void saveModel(OutputStream outputStream, DiseaseCaseModel model) throws IOException;

    /**
     * Read model instance with specified name from resource location. Return empty optional in case of I/O error or
     * absence of model.
     *
     * @param inputStream with model data
     * @return {@link DiseaseCaseModel} containing read data
     * @throws FileNotFoundException if
     */
    DiseaseCaseModel readModel(InputStream inputStream) throws IOException;

    /**
     * Return a collection with names of model files that are present in the model files directory.
     *
     * @return {@link Collection} of Strings of model filenames (with suffixes)
     */
    Collection<File> getModelNames();
}
