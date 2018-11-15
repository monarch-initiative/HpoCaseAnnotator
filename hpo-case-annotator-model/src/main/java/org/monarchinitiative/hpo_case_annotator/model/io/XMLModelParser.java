package org.monarchinitiative.hpo_case_annotator.model.io;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.Codecs;
import org.monarchinitiative.hpo_case_annotator.model.xml_model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;
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
    public static void saveDiseaseCase(DiseaseCase model, OutputStream outputStream) {
        try (XMLEncoder xmlEncoder = new XMLEncoder(outputStream)) {
            DiseaseCaseModel dcm = Codecs.diseaseCase2DiseaseCaseModel(model);
            xmlEncoder.writeObject(dcm);
        }
    }


    /**
     * Try to deserialize content of the provided <code>inputStream</code> and reconstruct {@link DiseaseCaseModel}
     * instance. Data must be in XML format.
     *
     * @param inputStream pointing to data stored in XML format is stored
     * @return {@link DiseaseCaseModel} with read data
     */
    public static DiseaseCase loadDiseaseCase(InputStream inputStream) {
        try (XMLDecoder xmlDecoder = new XMLDecoder(inputStream)) {
            DiseaseCaseModel dcm = (DiseaseCaseModel) xmlDecoder.readObject();
            return Codecs.diseaseCaseModel2DiseaseCase(dcm);
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
    public void saveModel(OutputStream outputStream, DiseaseCase model) throws IOException {
        saveDiseaseCase(model, outputStream);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DiseaseCase readModel(InputStream inputStream) {
        return loadDiseaseCase(inputStream);
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
