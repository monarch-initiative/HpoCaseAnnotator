package org.monarchinitiative.hpo_case_annotator.io;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class uses {@link XMLEncoder} & {@link XMLDecoder} to load/dump model data into XML file.<p>Note: no alert
 * windows are created here, upper-level code should handle that.
 */
public final class XMLModelParser implements ModelParser {

    public static final String MODEL_SUFFIX = ".xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLModelParser.class);

    /**
     * Path to directory where XML files containing model data are stored.
     */
    private final ObjectProperty<File> modelDir = new SimpleObjectProperty<>(this, "modelDir");


    /**
     * Create parser that will read and save model XML files to provided directory.
     *
     */
    public XMLModelParser(File modelDir) {
        this.modelDir.set(modelDir);
    }


    /**
     * Serialize {@link DiseaseCaseModel} data in XML format into given file.
     *
     * @param model        {@link DiseaseCaseModel} containing data to be serializec.
     * @param outputStream where data will be serialized in XML format
     */
    public static void saveDiseaseCaseModel(DiseaseCaseModel model, OutputStream outputStream) {
        try (XMLEncoder xmlEncoder = new XMLEncoder(outputStream)) {
            xmlEncoder.writeObject(model);
        }
    }


    /**
     * Try to deserialize content of the provided <code>inputStream</code> and reconstruct {@link DiseaseCaseModel}
     * instance. Data must be in XML format.
     *
     * @param inputStream pointing to data stored in XML format is stored
     * @return {@link DiseaseCaseModel} with read data
     */
    public static DiseaseCaseModel loadDiseaseCaseModel(InputStream inputStream) {
        try (XMLDecoder xmlDecoder = new XMLDecoder(inputStream)) {
            return ((DiseaseCaseModel) xmlDecoder.readObject());
        }
    }


    public File getModelDir() {
        return modelDir.get();
    }


    public void setModelDir(File modelDir) {
        this.modelDir.set(modelDir);
    }


    public ObjectProperty<File> modelDirProperty() {
        return modelDir;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveModel(OutputStream outputStream, DiseaseCaseModel model) {
        try (XMLEncoder xmlEncoder = new XMLEncoder(outputStream)) {
            xmlEncoder.writeObject(model);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DiseaseCaseModel readModel(InputStream inputStream) {
        return loadDiseaseCaseModel(inputStream);
    }


    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Collection<File> getModelNames() {
        if (modelDir.get() == null) {
            return new HashSet<>();
        }
        File[] files = modelDir.get().listFiles(f -> f.getName().endsWith(MODEL_SUFFIX));
        if (files == null) {
            return new HashSet<>();
        }
        return Arrays.asList(files);
    }
}
