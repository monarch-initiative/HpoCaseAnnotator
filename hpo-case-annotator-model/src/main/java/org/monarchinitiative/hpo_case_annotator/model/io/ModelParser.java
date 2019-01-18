package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

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
     * @return {@link DiseaseCase} containing read data or <code>null</code> if the data could not be read correctly
     */
    Optional<DiseaseCase> readModel(InputStream inputStream);

    /**
     * Return a collection with names of model files that are present in the model files directory.
     *
     * @return {@link Collection} of Strings of model filenames (with suffixes)
     */
    Collection<File> getModelNames();
}
