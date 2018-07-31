package org.monarchinitiative.hpo_case_annotator.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.model.Variant;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class PhenoModelExporter implements ModelExporter {

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


    public PhenoModelExporter(String modelDir, String delimiter) {
        this.modelDir = new File(modelDir);
        this.delimiter = delimiter;
        log.info(String.format("Initialized PhenoModelExporter using %s as delimiter and %s as model directory",
                delimiter, modelDir));
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


    @Override
    public void exportModels(Writer writer) {
        File[] models = modelDir.listFiles(f -> f.getName().endsWith(XML_MODEL_SUFFIX));
        if (models == null)
            return;
        Collection<DiseaseCaseModel> cases = Arrays.stream(models)
                .map(XMLModelParser::loadDiseaseCaseModel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.trace("exportModels");

        String header = "#ID\t#SYMBOL\tPMID\tF_AUTH\tID_SUMMARY\tVARIANTS\tHPO";
        int c = 0;
        try {
            writer.write(header + "\n");
            for (DiseaseCaseModel csmod : cases) {
                c++;
                String patientID = String.format("P%d-%s\t", c, csmod.getTargetGene().getGeneName());
                writer.write(patientID);
                exportModel(csmod, writer);
            }
        } catch (IOException e) {
            log.warn("Error writing header.");
            log.warn(e);
        }
    }


    /**
     * This export format is designed for the GPI study. It should be called by
     * {@link #exportModels(Writer)}. The header should be
     * <pre>
     *     GeneSymbol--PMID--FirstAuthor--Variants--HPO1;HPO2;...
     * </pre>
     */
    void exportModel(DiseaseCaseModel model, Writer writer) throws IOException {
        // ID_SUMMARY: author;year;gene_symbol;patient_id
        String id_summary = String.format("%s;%s;%s;%s",
                model.getPublication().getFirstAuthorSurname(),
                model.getPublication().getYear(),
                model.getTargetGene().getGeneName(),
                model.getFamilyInfo().getFamilyOrPatientID());

        writer.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\n",
                model.getTargetGene().getGeneName(),
                model.getPublication().getPmid(),
                model.getPublication().getFirstAuthorSurname(),
                id_summary,
                getVariantString(model),
                getPhenoString(model)));
        log.trace(String.format("%s\t%s\t%s\t%s\t%s\t%s\n",
                model.getTargetGene().getGeneName(),
                model.getPublication().getPmid(),
                model.getPublication().getFirstAuthorSurname(),
                id_summary,
                getVariantString(model),
                getPhenoString(model)));
    }


    /**
     * This export format is designed for the GPI study. It outputs the mutations strings.
     */
    String getVariantString(DiseaseCaseModel model) {
        StringBuilder sb = new StringBuilder();
        boolean second = false;
        for (Variant v : model.getVariants()) {
            String vs = String.format("%s:%s%s>%s[%s,%s%s]", v.getChromosome(),
                    v.getPosition(), v.getReferenceAllele(), v.getAlternateAllele(), v.getGenotype(), v.getVariantClass(), v.getPathomechanism());
            if (second) sb.append(";");
            else second = true;
            sb.append(vs);
        }
        return sb.toString();
    }


    /**
     * This export format is designed for the GPI study. It outputs
     * a semicolon-separated list of HPO terms.
     */
    String getPhenoString(DiseaseCaseModel model) {
        try {
            if (model.getHpoList() == null) {
                return String.format("WARNING: No HPO terms found for model: %s", model);
            } else {
                return model.getHpoList().stream().filter(hpo -> hpo.getObserved().equals("YES")).map(hpo -> hpo.getHpoId())
                        .collect(Collectors.joining(";"));
            }
        } catch (Exception e) {
            System.err.println(String.format("Trouble exporting model for %s", model.getPublication().toString()));
            return "No HPOs(Warning)";
        }
    }

}
