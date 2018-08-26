package org.monarchinitiative.hpo_case_annotator.io;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TSVModelExporterTest {

    private static final String MODEL_DIR_PATH = "target/test-classes/models/xml";

    private static final String FIELD_DELIMITER = "\t";

    private TSVModelExporter exporter;


    @Before
    public void setUp() throws Exception {
        exporter = new TSVModelExporter(MODEL_DIR_PATH, FIELD_DELIMITER);
    }


    @Test
    public void exportModels() throws Exception {
        StringWriter writer = new StringWriter();
        exporter.exportModels(writer);
        Set<String> lines = new HashSet<>();
        lines.addAll(Arrays.asList("#CHROM\tPOS\tREF\tALT\tGT\tVCLASS\tPATHOM\tCONSQ\tCSSTYPE\tCSSPOS\tSYMBOL\tPMID\tFILE_NAME" + System.lineSeparator(),
                "7\t10490\tA\tCC\theterozygous\tcoding\tcoding|missense\tExon skipping\tnull\tnull\tNF1\t10607834\tArs-2000-NF1" + System.lineSeparator(),
                "11\t61719380\tC\tT\theterozygous\tsplicing\tsplicing|5ss|disrupted\tAlternative/cryptic 5' splice site\t5 splice site\t61719378\tBEST1\t21203346\tDavidson-2010-BEST1" + System.lineSeparator(),
                "11\t61724406\tT\tC\theterozygous\tcoding\tcoding|missense\tN/A\tN/A\tN/A\tBEST1\t21203346\tDavidson-2010-BEST1" + System.lineSeparator(),
                "9\t123737057\tC\tT\thomozygous\tsplicing\tsplicing|3css|activated\tExon skipping\t5' splice site\t123737090\tC5\t19375167\tAguilar-Ramirez-2009-C5" + System.lineSeparator(),
                "9\t123737057\tC\tA\theterozygous\t5UTR\t5UTR|transcription\tN/A\tN/A\tN/A\tC5\t19375167\tAguilar-Ramirez-2009-C5" + System.lineSeparator(),
                "9\t123737057\tC\tG\thomozygous\tpromoter\tpromoter|reduced-transcription\tN/A\tN/A\tN/A\tC5\t19375167\tAguilar-Ramirez-2009-C5" + System.lineSeparator(),
                "1\t11940\tA\tG\theterozygous\tcoding\tcoding|missense\tExon skipping\tnull\tnull\tDDX11L1\t7514569\tHull-1994-DDX11L1" + System.lineSeparator()));
        assertTrue(lines.stream().allMatch(exp -> writer.toString().contains(exp)));
    }


    /**
     * Test that variants from models are correctly turned into lines (Strings).
     */
    @Test
    public void meltModelToVariants() throws Exception {
        List<String> expected = new ArrayList<>();
        expected.add("1\t11940\tA\tG\theterozygous\tcoding\tcoding|missense\tExon " +
                "skipping\tnull\tnull\tDDX11L1\t7514569\tHull-1994-DDX11L1");
        expected.add("11\t61719380\tC\tT\theterozygous\tsplicing\tsplicing|5ss|disrupted\tAlternative/cryptic 5' " +
                "splice site\t5 splice site\t61719378\tBEST1\t21203346\tDavidson-2010-BEST1");
        expected.add("11\t61724406\tT\tC\theterozygous\tcoding\tcoding|missense\tN/A\tN/A\tN/A\tBEST1\t21203346" +
                "\tDavidson-2010-BEST1");
        expected.add("7\t10490\tA\tCC\theterozygous\tcoding\tcoding|missense\tExon " +
                "skipping\tnull\tnull\tNF1\t10607834\tArs-2000-NF1");
        expected.add("9\t123737057\tC\tA\theterozygous\t5UTR\t5UTR|transcription\tN/A\tN/A\tN/A\tC5\t19375167" +
                "\tAguilar-Ramirez-2009-C5");
        expected.add("9\t123737057\tC\tG\thomozygous\tpromoter\tpromoter|reduced-transcription\tN/A\tN/A\tN/A\tC5" +
                "\t19375167\tAguilar-Ramirez-2009-C5");
        expected.add("9\t123737057\tC\tT\thomozygous\tsplicing\tsplicing|3css|activated\tExon skipping\t5' splice " +
                "site\t123737090\tC5\t19375167\tAguilar-Ramirez-2009-C5");

        List<String> actual = getModels().stream().map(exporter::meltModelToVariants)
                .flatMap(Set::stream).sorted().collect(Collectors.toList());
        assertEquals(expected, actual);
    }


    private List<DiseaseCaseModel> getModels() throws Exception {
        File[] files = new File(MODEL_DIR_PATH).listFiles(f -> f.getName().endsWith(".xml"));
        List<DiseaseCaseModel> models = new ArrayList<>();
        for (File file : files) {
            try (InputStream is = new FileInputStream(file)) {
                models.add(XMLModelParser.loadDiseaseCaseModel(is));
            }
        }
        return models;
    }

}