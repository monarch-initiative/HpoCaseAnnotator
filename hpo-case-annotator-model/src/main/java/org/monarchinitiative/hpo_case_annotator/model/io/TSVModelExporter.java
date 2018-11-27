package org.monarchinitiative.hpo_case_annotator.model.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TSVModelExporter implements ModelExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSVModelExporter.class);

    /**
     * String used to separate columns of the table.
     */
    private final String delimiter;


    /**
     * Create exporter that will export data from models in XML format present in provided directory.
     *
     * @param delimiter String to be used as column delimiter
     */
    public TSVModelExporter(String delimiter) {
        this.delimiter = delimiter;
        LOGGER.debug("Initialized TSVModelExporter using {} as delimiter", delimiter);
    }


    /**
     * Create exporter that will export data using a default delimiter <code>"\t"</code>.
     */
    public TSVModelExporter() {
        this("\t");
    }


    /**
     * Convert data from models into lines of the table. Iterate through variants present in model and create line per
     * variant. Each line contains fields with variant attributes, fields are separated using {@link #delimiter}.
     *
     * @return {@link Function} for converting {@link DiseaseCase} to {@link Set} of Strings representing individual
     * variants
     */
    private Function<DiseaseCase, Set<String>> modelToVariants() {
        return model -> model.getVariantList().stream()
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

                    return String.join(delimiter, fields);
                })
                .collect(Collectors.toSet());
    }


    /**
     * Extract variants from provided {@link DiseaseCase}s and write variant per line using provided
     * <code>writer</code>.
     *
     * @param writer {@link Writer} to be used for writing results
     */
    @Override
    public void exportModels(Collection<DiseaseCase> cases, Writer writer) {

        String header = "#CHROM\tPOS\tREF\tALT\tGT\tVCLASS\tPATHOM\tCONSQ\tCSSTYPE\tCSSPOS\tSYMBOL\tPMID\tFILE_NAME";
        try {
            writer.write(header + System.lineSeparator());
        } catch (IOException e) {
            LOGGER.warn("Error writing header", e);
            return;
        }
        cases.stream()
                .map(modelToVariants())
                .flatMap(Collection::stream)
                .forEach(variant -> {
                    try {
                        writer.write(variant + System.lineSeparator());
                    } catch (IOException e) {
                        LOGGER.warn("Error writing variant {}", variant, e);
                    }
                });
    }
}
