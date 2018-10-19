package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.SplicingVariant;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class TSVModelExporter implements ModelExporter {

    public static final String XML_MODEL_SUFFIX = ".xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(TSVModelExporter.class);

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
        LOGGER.debug(String.format("Initialized TSVModelExporter using %s as delimiter and %s as model directory",
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

        Collection<String> variants = new ArrayList<>();
        for (File path : models) {
            try (FileInputStream fis = new FileInputStream(path)) {
                DiseaseCase model = XMLModelParser.loadDiseaseCase(fis);
                variants.addAll(meltModelToVariants(model));
            } catch (IOException e) {
                LOGGER.warn("Error reading model file {}", path.getAbsolutePath(), e.getMessage());
            }
        }
        LOGGER.trace("exportModels");

        String header = "#CHROM\tPOS\tREF\tALT\tGT\tVCLASS\tPATHOM\tCONSQ\tCSSTYPE\tCSSPOS\tSYMBOL\tPMID\tFILE_NAME";
        try {
            writer.write(header + System.lineSeparator());
        } catch (IOException e) {
            LOGGER.warn("Error writing header.", e);
        }

        for (String variant : variants) {
            try {
                writer.write(variant + System.lineSeparator());
            } catch (IOException e) {
                LOGGER.warn(String.format("Error writing variant %s", variant));
                LOGGER.warn(e.getMessage());
            }
        }
    }


    /**
     * Convert data from models into lines of the table. Iterate through variants present in model and create line per
     * variant. Each line contains fields with variant attributes, fields are separated using {@link #delimiter}.
     *
     * @param model instance of {@link DiseaseCase} with data.
     * @return {@link Set} of Strings representing individual variants.
     */
    Set<String> meltModelToVariants(DiseaseCase model) {
        return model.getVariantList().stream()
                .map(var -> {
                    List<String> fields = new ArrayList<>();
                    fields.add(var.getContig());
                    fields.add(String.valueOf(var.getPos()));
                    fields.add(var.getRefAllele());
                    fields.add(var.getAltAllele());
                    fields.add(var.getGenotype().toString());
                    fields.add(var.getVariantClass());
                    fields.add(var.getPathomechanism());
                    fields.add(var.getConsequence().equals("") ? "N/A" : var.getConsequence());
                    fields.add(var.getCrypticSpliceSiteType().toString());
                    fields.add(var.getCrypticPosition() == 0 ? "NaN" : String.valueOf(var.getCrypticPosition()));
                    fields.add(model.getGene().getSymbol());
                    fields.add(model.getPublication().getPmid());
                    fields.add(Utils.getNameFor(model));

                    return fields.stream().collect(Collectors.joining(delimiter));
                })
                .collect(Collectors.toSet());
    }
}
