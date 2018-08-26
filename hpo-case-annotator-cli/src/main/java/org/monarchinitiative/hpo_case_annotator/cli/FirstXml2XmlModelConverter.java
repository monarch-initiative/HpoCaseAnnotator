package org.monarchinitiative.hpo_case_annotator.cli;

import org.monarchinitiative.hpo_case_annotator.io.FirstModelXMLParser;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class converts XML files storing data in the <em>first</em> XML format into the format using Java beans.
 *
 * The class reads XML files using {@link FirstModelXMLParser} and writes the files using {@link XMLModelParser}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
class FirstXml2XmlModelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstXml2XmlModelConverter.class);

    private final ModelParser inputParser, outputParser;

    private final File destDir, sourceDir;


    FirstXml2XmlModelConverter(File sourceDir, File destDir) throws IOException {
        if (!sourceDir.isDirectory()) {
            throw new IOException("Not a directory: " + sourceDir.getAbsolutePath());
        } else if (!destDir.isDirectory()) {
            throw new IOException("Not a directory: " + destDir.getAbsolutePath());
        }
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        inputParser = new FirstModelXMLParser(sourceDir);
        outputParser = new XMLModelParser(destDir);
    }


    void run() throws Exception {
        LOGGER.info("Found {} files in directory '{}'", inputParser.getModelNames().size(), sourceDir.getAbsolutePath());
        for (File xmlPath : inputParser.getModelNames()) {
            LOGGER.info("Processing {}", xmlPath);
            DiseaseCaseModel model = inputParser.readModel(new FileInputStream(xmlPath));
            outputParser.saveModel(new FileOutputStream(new File(destDir, xmlPath.getName())), model);
        }
    }
}
