package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class TSVModelExporterTest {

    private static final String FIELD_DELIMITER = "\t";

    private static final Collection<DiseaseCase> cases = new HashSet<>();

    private TSVModelExporter exporter;


    @BeforeClass
    public static void setUpBefore() throws Exception {
        Path xmlModelDir = Paths.get(TestResources.TEST_XML_MODEL_FILE_DIR.toURI());
        FileFilter testedCasesFilter = f -> f.getName().endsWith(".xml") &&
                (f.getName().startsWith("Davidson") ||
                        f.getName().startsWith("Hull"));
        final File[] files = Objects.requireNonNull(xmlModelDir.toFile().listFiles(testedCasesFilter));

        for (File file : files) {
            try (InputStream is = new FileInputStream(file)) {
                XMLModelParser.loadDiseaseCase(is)
                        .ifPresent(cases::add);
            }
        }
    }


    @Before
    public void setUp() throws Exception {
        exporter = new TSVModelExporter(FIELD_DELIMITER);
    }


    @Test
    public void exportModels() throws Exception {
        StringWriter writer = new StringWriter();
        exporter.exportModels(cases, writer);

        Set<String> actual = new HashSet<>(Arrays.asList(writer.toString().split(System.lineSeparator())));
        assertThat(actual.size(), is(4));
        assertThat(actual, containsInAnyOrder(
                "#CHROM\tPOS\tREF\tALT\tGT\tVCLASS\tPATHOM\tCONSQ\tCSSTYPE\tCSSPOS\tSYMBOL\tPMID\tFILE_NAME",
                "11\t61719380\tC\tT\tHETEROZYGOUS\tsplicing\tsplicing|5ss|disrupted\tAlternative/cryptic 5' splice site\tFIVE_PRIME\t61719378\tBEST1\t21203346\tDavidson-2010-BEST1",
                "11\t61724406\tT\tC\tHETEROZYGOUS\tcoding\tcoding|missense\tN/A\tNO\tNaN\tBEST1\t21203346\tDavidson-2010-BEST1",
                "1\t11940\tA\tG\tHETEROZYGOUS\tcoding\tcoding|missense\tExon skipping\tNO\tNaN\tDDX11L1\t7514569\tHull-1994-DDX11L1"
        ));
    }
}