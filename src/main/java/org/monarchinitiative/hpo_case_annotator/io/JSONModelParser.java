package org.monarchinitiative.hpo_case_annotator.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

public final class JSONModelParser implements ModelParser {

    private static final Logger log = LoggerFactory.getLogger(JSONModelParser.class);

    private final ObjectMapper mapper;

    private final File modelDir;

    private String errorMessage;


    public JSONModelParser(File modelDir) {
        this.modelDir = modelDir;
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    public JSONModelParser(String modelDirPath) {
        this(new File(modelDirPath));
    }


    public File getModelDir() {
        return modelDir;
    }


    /**
     * Save provided model object into file with the specified name. The file is saved into modelDir.
     *
     * @param name  name of the created file. Suffix <em>.json</em> will be appended, if not present.
     * @param model model object to be saved.
     * @return true if the file was saved.
     */
    @Override
    public boolean saveModel(String name, DiseaseCaseModel model) {
        String fname = (name.endsWith(".json")) ? name : name + ".json";
        boolean status;
        try (Writer writer = new BufferedWriter(new FileWriter(new File(modelDir, fname)))) {
            status = this.saveModel(writer, model);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            log.warn(e.getMessage());
            status = false;
        }
        return status;
    }


    /**
     * Save provided model object using provided Writer.
     *
     * @param writer Writer instance where the data will be written in JSON format.
     * @param model  {@link DiseaseCaseModel} with data to be saved.
     * @return true if the model object was saved successfully.
     */
    @Override
    public boolean saveModel(Writer writer, DiseaseCaseModel model) {
        try {
            mapper.writeValue(writer, model);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            log.warn(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * Parse the file and return {@link DiseaseCaseModel} object if the file is present.
     *
     * @param fileName basename of the file (e.g. fileName.json)
     * @return Optional with {@link DiseaseCaseModel} if parsing went ok.
     */
    @Override
    public Optional<DiseaseCaseModel> readModel(String fileName) {
        String fname = (fileName.endsWith(".json")) ? fileName : fileName + ".json";
        try (Reader reader = new BufferedReader(new FileReader(new File(modelDir, fname)))) {
            return Optional.of(mapper.readValue(reader, DiseaseCaseModel.class));
        } catch (IOException e) {
            errorMessage = e.getMessage();
            System.err.println(errorMessage);
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public Collection<String> getModelNames() {
//        TODO - implement me
        throw new RuntimeException("Not yet implemented");
    }


    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

}
