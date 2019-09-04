package org.monarchinitiative.hpo_case_annotator.cli.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.hpo_case_annotator.model.io.ProtoJSONModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.*;
import org.monarchinitiative.hpo_case_annotator.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Read all the JSON files and write data regarding variants into a table.
 * <p>
 * Each line of the file represents a single variant with following columns:
 * <ul>
 * <li>FILE NAME</li> - e.g. Pousada-2017-BMPR2
 * <li>SYMBOL</li> - HGNC gene symbol
 * <li>ASSEMBLY</li> - version of the reference genome
 * <li>CONTIG</li> - contig number, e.g. 1, 2, 3, ..., X
 * <li>POS</li> - 1-based coordinate of the variant in VCF style
 * <li>REF</li> - reference allele
 * <li>ALT</li> - alternate allele
 * <li>GT</li> - genotype
 * <li>VCLASS</li> - variant class
 * <li>PATHOMECHANISM</li> - pathomechanism
 * <li>CSQ</li> - consequence
 * </ul>
 * </p>
 */
@Parameters(commandDescription = "Create table with stats")
public class StatsCommand extends AbstractNamedCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsCommand.class);

    @Parameter(names = {"-m", "--model-dir"}, required = true, description = "Path to directory with JSON model files")
    private String modelDirectoryPath;

    @Parameter(names = {"-o", "--output"}, required = true, description = "Path where the result file will be written")
    private String outputPathString;

    @Parameter(names = {"-d", "--delimiter"}, description = "Column separator for the output file")
    private String delimiter = "\t";

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getModelDirectoryPath() {
        return modelDirectoryPath;
    }

    public void setModelDirectoryPath(String modelDirectoryPath) {
        this.modelDirectoryPath = modelDirectoryPath;
    }

    public String getOutputPathString() {
        return outputPathString;
    }

    public void setOutputPathString(String outputPathString) {
        this.outputPathString = outputPathString;
    }

    @Override
    public void run() {
        File modelDirPath = new File(modelDirectoryPath);
        if (!modelDirPath.isDirectory()) {
            LOGGER.error("Path {} does not point to directory", modelDirectoryPath);
            return;
        }

        File[] modelsArray = modelDirPath.listFiles(f -> f.getName().endsWith(".json"));
        if (modelsArray == null) {
            LOGGER.error("Some weird error has happened. Investigate..");
            return;
        }

        LOGGER.info("Found {} cases in '{}' folder", modelsArray.length, modelDirPath.getAbsolutePath());
        Path outputPath = Paths.get(outputPathString);
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            String header = new StringBuilder()
                    .append("FILE_NAME").append(delimiter)
                    .append("PROBAND").append(delimiter)
                    .append("SEX").append(delimiter)
                    .append("PHENOTYPE_COUNT").append(delimiter)
                    .append("SYMBOL").append(delimiter)
                    .append("ASSEMBLY").append(delimiter)
                    .append("CONTIG").append(delimiter)
                    .append("POS").append(delimiter)
                    .append("REF").append(delimiter)
                    .append("ALT").append(delimiter)
                    .append("GT").append(delimiter)
                    .append("VCLASS").append(delimiter)
                    .append("PATHOMECHANISM").append(delimiter)
                    .append("CSQ").append(delimiter)
                    .toString();
            writer.write(header);
            writer.newLine();
            for (File file : modelsArray) {
                DiseaseCase dc;
                try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                    Optional<DiseaseCase> dco = ProtoJSONModelParser.readDiseaseCase(reader);
                    if (!dco.isPresent()) {
                        LOGGER.error("Unable to read model file {}", file.getPath());
                        continue;
                    }
                    dc = dco.get();
                }
                String fileName = ModelUtils.getFileNameFor(dc);
                String symbol = dc.getGene().getSymbol();
                FamilyInfo fi = dc.getFamilyInfo();
                String probandId = fi.getFamilyOrProbandId();
                Sex sex = fi.getSex();

                int phenotypeCount = dc.getPhenotypeCount();

                for (Variant variant : dc.getVariantList()) {
                    StringBuilder builder = new StringBuilder();
                    VariantPosition vp = variant.getVariantPosition();

                    // make the line
                    builder.append(fileName).append(delimiter)
                            .append(probandId).append(delimiter)
                            .append(sex).append(delimiter)
                            .append(phenotypeCount).append(delimiter)
                            .append(symbol).append(delimiter)
                            .append(vp.getGenomeAssembly()).append(delimiter)
                            .append(vp.getContig()).append(delimiter)
                            .append(vp.getPos()).append(delimiter)
                            .append(vp.getRefAllele()).append(delimiter)
                            .append(vp.getAltAllele()).append(delimiter)
                            .append(variant.getGenotype()).append(delimiter)
                            .append(variant.getVariantClass()).append(delimiter)
                            .append(variant.getPathomechanism()).append(delimiter)
                            .append(variant.getConsequence()).append(delimiter);

                    // write the line
                    writer.write(builder.toString());
                    writer.newLine();
                }


            }
        } catch (IOException e) {
            LOGGER.error("Error:", e);
        }
        LOGGER.info("Wrote stats into '{}'", outputPath);
    }

    @Override
    public String getCommandName() {
        return "stats";
    }
}
