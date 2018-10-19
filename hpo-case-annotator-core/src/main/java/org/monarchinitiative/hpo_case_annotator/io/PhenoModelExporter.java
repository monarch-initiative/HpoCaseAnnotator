package org.monarchinitiative.hpo_case_annotator.io;

import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.OntologyClass;
import org.monarchinitiative.hpo_case_annotator.model.proto.Utils;
import org.monarchinitiative.hpo_case_annotator.model.proto.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 */
public class PhenoModelExporter implements ModelExporter {

    public static final String XML_MODEL_SUFFIX = ".xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenoModelExporter.class);

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
        LOGGER.debug(String.format("Initialized PhenoModelExporter using %s as delimiter and %s as model directory",
                delimiter, modelDir));
    }


    @Override
    public void exportModels(Writer writer) {
        File[] models = modelDir.listFiles(f -> f.getName().endsWith(XML_MODEL_SUFFIX));
        if (models == null)
            return;
        Collection<DiseaseCase> cases = new ArrayList<>();
        for (File path : models) {
            try (FileInputStream fis = new FileInputStream(path)) {
                DiseaseCase model = XMLModelParser.loadDiseaseCase(fis);
                cases.add(model);
            } catch (IOException e) {
                LOGGER.warn("Error reading model file {}", path.getAbsolutePath(), e.getMessage());
            }
        }

        LOGGER.trace("exportModels");

        String header = "#ID\t#SYMBOL\tPMID\tF_AUTH\tID_SUMMARY\tVARIANTS\tHPO";
        int c = 0;
        try {
            writer.write(header + "\n");
            for (DiseaseCase csmod : cases) {
                c++;
                String patientID = String.format("P%d-%s\t", c, csmod.getGene().getSymbol());
                writer.write(patientID);
                exportModel(csmod, writer);
            }
        } catch (IOException e) {
            LOGGER.warn("Error writing header.", e);
        }
    }


    /**
     * This export format is designed for the GPI study. It should be called by
     * {@link #exportModels(Writer)}. The header should be
     * <pre>
     *     GeneSymbol--PMID--FirstAuthor--Variants--HPO1;HPO2;...
     * </pre>
     */
    void exportModel(DiseaseCase model, Writer writer) throws IOException {
        // ID_SUMMARY: author;year;gene_symbol;patient_id
        String id_summary = String.format("%s;%s;%s;%s",
                Utils.getFirstAuthorsSurname(model.getPublication()),
                model.getPublication().getYear(),
                model.getGene().getSymbol(),
                model.getFamilyInfo().getFamilyOrProbandId());

        writer.write(String.format("%s\t%s\t%s\t%s\t%s\t%s\n",
                model.getGene().getSymbol(),
                model.getPublication().getPmid(),
                Utils.getFirstAuthorsSurname(model.getPublication()),
                id_summary,
                getVariantString(model),
                getPhenoString(model)));
        LOGGER.trace(String.format("%s\t%s\t%s\t%s\t%s\t%s\n",
                model.getGene().getSymbol(),
                model.getPublication().getPmid(),
                Utils.getFirstAuthorsSurname(model.getPublication()),
                id_summary,
                getVariantString(model),
                getPhenoString(model)));
    }


    /**
     * This export format is designed for the GPI study. It outputs the mutations strings.
     */
    private String getVariantString(DiseaseCase model) {
        StringBuilder sb = new StringBuilder();
        boolean second = false;
        for (Variant v : model.getVariantList()) {
            String vs = String.format("%s:%s%s>%s[%s,%s%s]", v.getContig(),
                    v.getPos(), v.getRefAllele(), v.getAltAllele(), v.getGenotype(), v.getVariantClass(), v.getPathomechanism());
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
    private String getPhenoString(DiseaseCase model) {
        try {
            if (model.getPhenotypeList() == null) {
                return String.format("WARNING: No HPO terms found for model: %s", model);
            } else {
                return model.getPhenotypeList().stream()
                        .filter(oc -> !oc.getNotObserved())
                        .map(OntologyClass::getId)
                        .collect(Collectors.joining(";"));
            }
        } catch (Exception e) {
            System.err.println(String.format("Trouble exporting model for %s", model.getPublication().toString()));
            return "No HPOs(Warning)";
        }
    }

}
