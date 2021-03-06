package org.monarchinitiative.hpo_case_annotator.cli.converters;

import org.monarchinitiative.hpo_case_annotator.model.io.ProtoJSONModelParser;
import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;

/**
 * This class is responsible for converting XML files to conform to the most recent data model format.
 */
public class Xml2JsonModelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Xml2JsonModelConverter.class);

    private final File sourceDir;

    private final ProtoJSONModelParser jsonModelParser;

    private final File destDir;


    public Xml2JsonModelConverter(File sourceDir, File destDir) {
        this.sourceDir = sourceDir;
        this.jsonModelParser = new ProtoJSONModelParser(destDir.toPath());
        this.destDir = destDir;
    }


    public void run() {
        File[] models = sourceDir.listFiles(f -> f.getName().endsWith(XMLModelParser.MODEL_SUFFIX));
        for (File modelPath : models) {
            File outPath = new File(destDir, modelPath.getName().replace(".xml", ".json"));
            try (InputStream inputStream = new FileInputStream(modelPath); OutputStream outputStream = new FileOutputStream(outPath)) {
                final Optional<DiseaseCase> diseaseCase = XMLModelParser.loadDiseaseCase(inputStream);
                if (diseaseCase.isPresent()) {
                    jsonModelParser.saveModel(outputStream, diseaseCase.get());
                }
            } catch (IOException e) {
                LOGGER.warn("Error parsing file {}", modelPath.getAbsolutePath(), e.getMessage());
            }
        }
    }

}
