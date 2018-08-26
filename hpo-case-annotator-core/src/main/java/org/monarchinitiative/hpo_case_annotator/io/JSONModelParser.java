package org.monarchinitiative.hpo_case_annotator.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public final class JSONModelParser implements ModelParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONModelParser.class);

    private final ObjectMapper mapper;

    private final File modelDir;


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
     * Save provided model object using provided Writer.
     *
     * @param outputStream instance where the data will be written in JSON format.
     * @param model  {@link DiseaseCaseModel} with data to be saved.
     */
    @Override
    public void saveModel(OutputStream outputStream, DiseaseCaseModel model) throws IOException {
        mapper.writeValue(outputStream, model);
    }


    /**
     * Parse the file and return {@link DiseaseCaseModel} object if the file is present.
     *
     * @param inputStream containing data formatted in JSON format
     * @return Optional with {@link DiseaseCaseModel} if parsing went ok
     */
    @Override
    public DiseaseCaseModel readModel(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, DiseaseCaseModel.class);
    }


    @Override
    public Collection<File> getModelNames() {
//        TODO - implement me
        throw new RuntimeException("Not yet implemented");
    }

}
