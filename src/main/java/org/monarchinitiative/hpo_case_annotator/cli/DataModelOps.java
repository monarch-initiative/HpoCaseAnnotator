package org.monarchinitiative.hpo_case_annotator.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * CLI interface for performing batch operations over data model files.
 */
public class DataModelOps {

    private static final Logger log = LoggerFactory.getLogger(DataModelOps.class);

    /**
     * Path to directory containing data model files that are subject to batch operation.
     */
    private final File sourceDir;

    /**
     * Path to directory where processed data model files will be stored.
     */
    private final File destDir;

    /**
     * String with name of the operation to be performed.
     */
    private final String operationName;


    public DataModelOps(String sourceDirPath, String destDirPath, String opName) {
        String errMsg = null;

        this.sourceDir = new File(sourceDirPath);
        this.destDir = new File(destDirPath);
        this.operationName = opName;

        if (!(sourceDir.exists() && sourceDir.isDirectory()))
            errMsg = String.format("%s either doesn't exist or is not a valid path to directory", sourceDirPath);

        if (!(destDir.exists() && destDir.isDirectory()))
            errMsg = String.format("%s either doesn't exist or is not a valid path to directory", destDirPath);

        if (errMsg != null) {
            System.err.println(errMsg);
            System.exit(1);
        }
    }


    public static void main(String[] argv) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("hrmd-data-model-ops");
        parser.description("Perform batch operations with data model files.");
        parser.version(getVersion());
        parser.addArgument("--version").help("Show hrmd-data-model-ops version").action(Arguments.version());
        parser.addArgument("operation").choices("xml2json").type(String.class).help("Operation to be performed " +
                "on data model files");
        parser.addArgument("sourcedir").type(String.class).help("Path to directory " +
                "containing data model files that are subject to batch operation");
        parser.addArgument("destdir").type(String.class).help("Path to directory where " +
                "processed data model files will be stored");

        parser.defaultHelp(true);
        parser.epilog("Hell yeah");

        Namespace args = null;
        try {
            args = parser.parseArgs(argv);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        DataModelOps dmo = new DataModelOps(args.getString("sourcedir"), args.getString("destdir"), args.getString
                ("operation"));
        dmo.run();
    }


    public static String getVersion() {
        return DataModelOps.class.getPackage().getSpecificationVersion();
    }


    /**
     * Perform the selected operation.
     */
    public void run() {
        switch (operationName) {
            case "xml2json":
                log.info(String.format("Running %s operation", operationName));
                Xml2JsonModelConverter modelConverter = new Xml2JsonModelConverter(sourceDir, destDir);
                modelConverter.run();
                break;
            default:
                // Shouldn't get here since ArgumentParser checks the operation names.
                log.info(String.format("Unknown operation: %s", operationName));
                System.exit(1);
        }
    }

}
