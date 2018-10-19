package org.monarchinitiative.hpo_case_annotator.cli;

import org.monarchinitiative.hpo_case_annotator.io.JSONModelParser;
import org.monarchinitiative.hpo_case_annotator.io.ProtoJSONModelParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This class is responsible for converting XML files to conform to the most recent data model format.
 */
public class Xml2JsonModelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Xml2JsonModelConverter.class);

    private final File sourceDir;

    private final ProtoJSONModelParser jsonModelParser;


    Xml2JsonModelConverter(File sourceDir, File destDir) {
        this.sourceDir = sourceDir;
        this.jsonModelParser = new ProtoJSONModelParser(destDir.toPath());
    }


    void run() {
        File[] models = sourceDir.listFiles(f -> f.getName().endsWith(XMLModelParser.MODEL_SUFFIX));
        for (File modelPath : models) {
            File outPath = new File(modelPath.getParentFile(), modelPath.getName().replace(".xml", ".json"));
            try (InputStream inputStream = new FileInputStream(modelPath); OutputStream outputStream = new FileOutputStream(outPath)) {
                DiseaseCase model = XMLModelParser.loadDiseaseCase(inputStream);
                jsonModelParser.saveModel(outputStream, model);
            } catch (IOException e) {
                LOGGER.warn("Error parsing file {}", modelPath.getAbsolutePath(), e.getMessage());
            }
        }
    }

}
