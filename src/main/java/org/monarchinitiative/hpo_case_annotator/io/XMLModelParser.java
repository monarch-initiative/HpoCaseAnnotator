package org.monarchinitiative.hpo_case_annotator.io;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.io.output.WriterOutputStream;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class uses {@link XMLEncoder} & {@link XMLDecoder} to load/dump model data into XML file.<p>Note: no alert
 * windows are created here, upper-level code should handle that.
 */
public final class XMLModelParser implements ModelParser {

    public static final String MODEL_SUFFIX = ".xml";

    private static final Logger log = LoggerFactory.getLogger(XMLModelParser.class);

    /**
     * Path to directory where XML files containing model data are stored.
     */
    private final ObjectProperty<File> modelDir = new SimpleObjectProperty<>(this, "modelDir");

    /**
     * Store message describing problems encountered during handling of model data.
     */
    private String errorMessage;


    /**
     * Create parser that will read and save model XML files to provided directory.
     *
     * @param optionalResources bean containing (among other things) path to folder with models
     */
    @Inject
    public XMLModelParser(OptionalResources optionalResources) {
        modelDir.bind(optionalResources.diseaseCaseDirProperty());
    }


    /**
     * Serialize {@link DiseaseCaseModel} data in XML format into given file.
     *
     * @param model       {@link DiseaseCaseModel} containing data to be serializec.
     * @param whereToSave path to file where the XML data will be serialized.
     * @return true if serialization went without errors, false if otherwise.
     */
    public static boolean saveDiseaseCaseModel(DiseaseCaseModel model, File whereToSave) {
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(whereToSave)));
            xmlEncoder.writeObject(model);
            xmlEncoder.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * Try to deserialize content of the provided file and reconstruct {@link DiseaseCaseModel} instance. File must be
     * in XML format.
     *
     * @param whereToLoadFrom path to file where data in XML format is stored.
     * @return {@link Optional} of {@link DiseaseCaseModel} object.
     */
    public static Optional<DiseaseCaseModel> loadDiseaseCaseModel(File whereToLoadFrom) {
        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(whereToLoadFrom)));
            DiseaseCaseModel model = (DiseaseCaseModel) xmlDecoder.readObject();
            xmlDecoder.close();
            return Optional.of(model);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return Optional.empty();
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param name  String with name to use to save the model data (without suffix).
     * @param model {@link DiseaseCaseModel} with data to be saved.
     * @return
     */
    @Override
    public boolean saveModel(String name, DiseaseCaseModel model) {
        return saveDiseaseCaseModel(model, new File(modelDir.get(), name));
    }


    /**
     * {@inheritDoc}
     *
     * @param writer Writer instance where the data will be written in JSON format.
     * @param model  {@link DiseaseCaseModel} with data to be saved.
     * @return
     */
    @Override
    public boolean saveModel(Writer writer, DiseaseCaseModel model) {
        try (XMLEncoder xmlEncoder = new XMLEncoder(new WriterOutputStream(writer))) {
            xmlEncoder.writeObject(model);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.warn(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * {@inheritDoc}
     *
     * @param fileName String with name of the model file (without suffix).
     * @return
     */
    @Override
    public Optional<DiseaseCaseModel> readModel(String fileName) {
        File where = new File(modelDir.get(), fileName);
        if (!where.isFile()) {
            log.warn(String.format("Provided path %s does not point to a file", where.getPath()));
            return Optional.empty();
        }
        if (!where.getName().endsWith(MODEL_SUFFIX)) {
            String msg = String.format("File %s does not end with expected suffix %s", fileName, MODEL_SUFFIX);
            log.warn(msg);
            errorMessage = msg;
            return Optional.empty();
        }
        return loadDiseaseCaseModel(where);
    }


    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Collection<String> getModelNames() {
        if (modelDir.get() == null) {
            return new HashSet<>();
        }
        File[] files = modelDir.get().listFiles(f -> f.getName().endsWith(MODEL_SUFFIX));
        if (files == null) {
            return new HashSet<>();
        }
        return Arrays.stream(files)
                .map(File::getName)
                .collect(Collectors.toSet());
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
