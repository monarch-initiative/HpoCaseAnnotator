package org.monarchinitiative.hpo_case_annotator.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class TSVModelExporter implements ModelExporter {

    public static final String XML_MODEL_SUFFIX = ".xml";

    private static final Logger log = LogManager.getLogger();

    /**
     * Path to directory with model files in XML format.
     */
    private final File modelDir;

    /**
     * String used to separate columns of the table.
     */
    private final String delimiter;


    /**
     * Create exporter that will export data from models in XML format present in provided directory.
     *
     * @param modelDir String with path to directory containing model XML files
     */
    public TSVModelExporter(String modelDir, String delimiter) {
        this.modelDir = new File(modelDir);
        this.delimiter = delimiter;
        log.info(String.format("Initialized TSVModelExporter using %s as delimiter and %s as model directory",
                delimiter, modelDir));
    }


    /**
     * Create exporter that will export data from models in XML format present in provided directory using a default
     * delimiter ("\t").
     *
     * @param modelDir String with path to directory containing model XML files
     */
    public TSVModelExporter(String modelDir) {
        this(modelDir, "\t");
    }


    @Override
    public void exportModels(String filePath) throws IOException {
        exportModels(new File(filePath));
    }


    @Override
    public void exportModels(File filePath) throws IOException {
        try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
            exportModels(writer);
        }
    }


    /**
     * Read model XML files, extract variants and write variant per line using provided writer.
     *
     * @param writer {@link Writer} to be used for writing results.
     */
    @Override
    public void exportModels(Writer writer) {
        File[] models = modelDir.listFiles(f -> f.getName().endsWith(XML_MODEL_SUFFIX));
        if (models == null)
            return;

        Collection<String> variants = Arrays.stream(models)
                .map(XMLModelParser::loadDiseaseCaseModel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::meltModelToVariants)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        log.trace("exportModels");

        String header = "#CHROM\tPOS\tREF\tALT\tGT\tVCLASS\tPATHOM\tCONSQ\tCSSTYPE\tCSSPOS\tSYMBOL\tPMID\tFILE_NAME";
        try {
            writer.write(header + System.lineSeparator());
        } catch (IOException e) {
            log.warn("Error writing header.");
            log.warn(e);
        }

        for (String variant : variants) {
            try {
                writer.write(variant + System.lineSeparator());
            } catch (IOException e) {
                log.warn(String.format("Error writing variant %s", variant));
                log.warn(e.getMessage());
            }
        }
    }


    /**
     * Convert data from models into lines of the table. Iterate through variants present in model and create line per
     * variant. Each line contains fields with variant attributes, fields are separated using {@link #delimiter}.
     *
     * @param model instance of {@link DiseaseCaseModel} with data.
     * @return {@link Set} of Strings representing individual variants.
     */
    Set<String> meltModelToVariants(DiseaseCaseModel model) {
        return model.getVariants().stream()
                .map(var -> {
                    List<String> fields = new ArrayList<>();
                    fields.add(var.getChromosome());
                    fields.add(var.getPosition());
                    fields.add(var.getReferenceAllele());
                    fields.add(var.getAlternateAllele());
                    fields.add(var.getGenotype());
                    fields.add(var.getVariantClass());
                    fields.add(var.getPathomechanism());
                    fields.add((var instanceof SplicingVariant) ? ((SplicingVariant) var).getConsequence() : "N/A");
                    fields.add((var instanceof SplicingVariant) ? ((SplicingVariant) var).getCrypticSpliceSiteType() : "N/A");
                    fields.add((var instanceof SplicingVariant) ? ((SplicingVariant) var).getCrypticPosition() : "N/A");
                    fields.add(model.getTargetGene().getGeneName());
                    fields.add(model.getPublication().getPmid());
                    fields.add(model.getFileName());

                    return fields.stream().collect(Collectors.joining(delimiter));
                })
                .collect(Collectors.toSet());
    }
}
