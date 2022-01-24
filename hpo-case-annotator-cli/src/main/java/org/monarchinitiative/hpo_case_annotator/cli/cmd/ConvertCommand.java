package org.monarchinitiative.hpo_case_annotator.cli.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Command for converting from older data formats into the newer ones.
 */
@Parameters(commandDescription = "Convert between data formats")
public class ConvertCommand extends AbstractNamedCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertCommand.class);

    /**
     * Path to directory containing data model files that are subject to batch operation.
     */
    @Parameter(names = {"-m", "--model-dir"}, required = true, description = "Path to directory with model files")
    private String modelDirectoryString;

    /**
     * Path to directory where processed data model files will be stored.
     */
    @Parameter(names = {"-o", "--output"}, required = true, description = "Path where the result file will be written")
    private String destDirString;

    /**
     * String with name of the operation to be performed.
     */
    @Parameter(names = {"-c", "--command"})
    private String operationName;

    @Override
    public String getCommandName() {
        return "convert";
    }

    @Override
    public void run() {
        String errMsg = null;

        File sourceDir = new File(modelDirectoryString);
        File destDir = new File(destDirString);

        if (!(sourceDir.exists() && sourceDir.isDirectory()))
            errMsg = String.format("%s either doesn't exist or is not a valid path to directory", modelDirectoryString);

        if (!(destDir.exists() && destDir.isDirectory()))
            errMsg = String.format("%s either doesn't exist or is not a valid path to directory", destDirString);

        if (errMsg != null) {
            System.err.println(errMsg);
            System.exit(1);
        }

        try {
            switch (operationName) {
//                case "xml2json": {
//                    LOGGER.info("Running {} operation", operationName);
//                    Xml2JsonModelConverter modelConverter = new Xml2JsonModelConverter(sourceDir, destDir);
//                    modelConverter.run();
//                    break;
//                }
//                case "firstxml2xml": {
//                    LOGGER.info("Running {} operation", operationName);
//                    FirstXml2XmlModelConverter modelConverter = new FirstXml2XmlModelConverter(sourceDir, destDir);
//                    modelConverter.run();
//                    break;
//                }
                default:
                    LOGGER.info("Unknown operation: {}", operationName);
                    System.exit(1);
            }
        } catch (Exception e) {
            LOGGER.warn("Error occurred during operation run", e);
            System.exit(1);
        }

        LOGGER.warn("This command is temporarily disabled");
    }
}
