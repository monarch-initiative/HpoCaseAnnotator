package org.monarchinitiative.hpo_case_annotator.model.io;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.model.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * These tests check the correct behaviour of the {@link PhenoModelExporter} class. In particular, <em>NOT</em> present
 * HPO terms should not be exported.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 1.0.6
 * @since 1.0
 */
public class PhenoModelExporterTest {


    private static final String DELIMITER = "\t";

    private static final Collection<DiseaseCase> cases = new HashSet<>();

    /**
     * Tested instance
     */
    private PhenoModelExporter exporter;


    @BeforeClass
    public static void setUpBefore() throws Exception {
        Path baroneCase = Paths.get(TestResources.TEST_GPI_MODEL_FILE_DIR.toURI()).resolve("Barone-2012-DPM2.xml");
        try (InputStream is = Files.newInputStream(baroneCase)) {
            DiseaseCase diseaseCase = XMLModelParser.loadDiseaseCase(is);
            cases.add(diseaseCase);
        }
    }


    @Before
    public void setUp() throws Exception {
        this.exporter = new PhenoModelExporter(DELIMITER);
    }


    /**
     * This test checks, that the <em>NOT</em> observed terms are not being included in the exported file. The file
     * <em></em> contains 36 observed HPO terms as well as 2 <em>NOT</em> observed:
     * <ul>
     * <li>Cerebellar atrophy (HP:0001272)</li>
     * <li>Suck reflex (HP:0030906)</li>
     * </ul>
     * The <em>NOT</em> observed terms must <em>NOT</em> be present in the record (GPI export TSV file) representing the
     * publication.
     */
    @Test
    public void negatedHPONotIncluded() {
        StringWriter writer = new StringWriter();
        exporter.exportModels(cases, writer);
        String record = Arrays.stream(writer.toString().split("\n"))
                .filter(line -> !line.startsWith("#"))
                .collect(Collectors.toList())
                .get(0);
        String hpoField = record.split(DELIMITER)[6];
        List<String> hpo = Arrays.stream(hpoField.split(";")).collect(Collectors.toList());
        // NOT observed terms not present
        assertThat(hpo, not(hasItem("HP:0001272")));
        assertThat(hpo, not(hasItem("HP:0030906")));
        // observed terms are present
        assertThat(hpo.size(), is(36)); // 36 observed HPO terms in the model Barone-2012-DPM2-patient1.xml
    }
}