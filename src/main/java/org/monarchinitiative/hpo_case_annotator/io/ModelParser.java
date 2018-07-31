package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.io.Writer;
import java.util.Collection;
import java.util.Optional;

public interface ModelParser {

    /**
     * Save the model data using provided name.
     *
     * @param name  String with name to use to save the model data.
     * @param model {@link DiseaseCaseModel} with data to be saved.
     * @return true if data were saved, false if otherwise.
     */
    boolean saveModel(String name, DiseaseCaseModel model);

    /**
     * Save the model data using provided writer.
     *
     * @param writer Writer instance where the data will be written in JSON format.
     * @param model  {@link DiseaseCaseModel} with data to be saved.
     * @return true if data were saved, false if otherwise.
     */
    boolean saveModel(Writer writer, DiseaseCaseModel model);

    /**
     * Read model instance with specified name from resource location. Return empty optional in case of I/O error or
     * absence of model.
     *
     * @param fileName String with name of the model file.
     * @return {@link Optional} with model data or empty optional in case of I/O error or absence of data.
     */
    Optional<DiseaseCaseModel> readModel(String fileName);

    /**
     * Return a collection with names of model files that are present at the resource location.
     *
     * @return {@link Collection} of Strings of model filenames.
     */
    Collection<String> getModelNames();

    /**
     * Return an error message describing the problems that were encountered during handling the model data.
     *
     * @return String with error message.
     */
    String getErrorMessage();
}
