package org.monarchinitiative.hpo_case_annotator.io;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpo_case_annotator.gui.application.HRMDResourceManager;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

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

    private static final Logger log = LogManager.getLogger();

    /**
     * Path to directory where XML files containing model data are stored.
     */
    private File modelDir;

    /**
     * Store message describing problems encountered during handling of model data.
     */
    private String errorMessage;

    private HRMDResourceManager resourceManager;


    /**
     * Create parser that will read and save model XML files to provided directory.
     *
     * @param modelDirPath location of the model XML files.
     */
    public XMLModelParser(String modelDirPath) {
        this.modelDir = new File(modelDirPath);
        log.info(String.format("XMLModelParser initialized with directory %s containing %d model files",
                modelDir.getPath(), getModelNames().size()));
    }


    public XMLModelParser(HRMDResourceManager resourceManager) {
        this.resourceManager = resourceManager;
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
     * Use {@link java.beans.XMLEncoder} to encode given bean to XML format and as String.
     */
    @Deprecated
    public static String getBeanAsString(Object model) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(baos);
        xmlEncoder.writeObject(model);
        xmlEncoder.close();
        return baos.toString();
    }


    @Deprecated
    public static boolean saveBeanToFile(Object bean, File whereToSave) {
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(whereToSave)));
            xmlEncoder.writeObject(bean);
            xmlEncoder.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        }
        return true;
    }


    @Deprecated
    public static Object loadBeanFromFile(File whereToLoadFrom) {
        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(whereToLoadFrom)));
            Object obj = xmlDecoder.readObject();
            xmlDecoder.close();
            return obj;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        }
    }


    private File getModelDir() {
        String modelDirPath = (resourceManager != null) ? resourceManager.getResources().getDiseaseCaseDir()
                : modelDir.getAbsolutePath();
        return new File(modelDirPath);
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
        return saveDiseaseCaseModel(model, new File(getModelDir(), name));
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
        File where = new File(getModelDir(), fileName);
        if (!(where.exists() && where.isFile())) {
            log.warn(String.format("Requested file %s either doesn't exist or is not a file", where.getPath()));
            return Optional.empty();
        }
        if (!where.getName().endsWith(MODEL_SUFFIX)) {
            String msg = String.format("File %s doesn't end with expected suffix %s", fileName, MODEL_SUFFIX);
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
        File[] files = getModelDir().listFiles(f -> f.getName().endsWith(MODEL_SUFFIX));
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
