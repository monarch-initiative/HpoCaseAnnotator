package org.monarchinitiative.hpo_case_annotator.cli;

import org.monarchinitiative.hpo_case_annotator.io.JSONModelParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * This class is responsible for converting XML files to conform to the most recent data model format.
 */
public class Xml2JsonModelConverter {

    private static final Logger log = LoggerFactory.getLogger(Xml2JsonModelConverter.class);

    private final File sourceDir;

    private final JSONModelParser jsonModelParser;


    public Xml2JsonModelConverter(File sourceDir, File destDir) {
        this.sourceDir = sourceDir;
        this.jsonModelParser = new JSONModelParser(destDir);
    }


    public File getSourceDir() {
        return sourceDir;
    }


    public JSONModelParser getJsonModelParser() {
        return jsonModelParser;
    }


    public void run() {
        File[] models = sourceDir.listFiles(f -> f.getName().endsWith(XMLModelParser.MODEL_SUFFIX));
        for (File modelPath : models) {

            Optional<DiseaseCaseModel> model = XMLModelParser.loadDiseaseCaseModel(modelPath);
            model.ifPresent(m -> jsonModelParser.saveModel(modelPath.getName().replace(".xml", ".json"), m));
        }
    }

}
