package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.core.TestResources;
import org.monarchinitiative.hpo_case_annotator.model.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore // TODO - make these tests work
public class ValidationRunnerTest {


    /**
     * Tested instance.
     */
    private ValidationRunner runner;


    @Before
    public void setUp() throws Exception {
        /*CompletenessValidator completenessValidator = new CompletenessValidator(variantValidator);
        GenomicPositionValidator genomicPositionValidator = new GenomicPositionValidator(TestResources.TEST_REF_GENOME_FASTA);
//        ModelParser modelParser = new XMLModelParser(TestResources.TEST_MODEL_FILE_DIR);
        runner = new ValidationRunner(genomicPositionValidator, completenessValidator);*/
    }


    /**
     * Test validation real-life model.
     */
    @Test
    public void testRunner() throws Exception {
        /*List<ValidationLine> validationLines = runner.validateModels(Collections.singleton(getArs()));
        assertEquals(2, validationLines.size());

        ValidationLine first = validationLines.get(0);
        assertEquals("Ars-2000-NF1", first.getModelName());
        assertEquals("CompletenessValidator", first.getValidatorName());
        assertEquals(ValidationResult.PASSED, first.getValidationResult());
        assertEquals("All right!", first.getErrorMessage());

        ValidationLine second = validationLines.get(1);
        assertEquals("Ars-2000-NF1", second.getModelName());
        assertEquals("GenomicPositionValidator", second.getValidatorName());
        assertEquals(ValidationResult.PASSED, second.getValidationResult());
        assertEquals("All right!", second.getErrorMessage());*/
    }


    /**
     * Utility method for creating DiseaseCase instance for testing.
     *
     * @return {@link DiseaseCase} instance for testing.
     */
    private DiseaseCase getArs() throws Exception {
        try (InputStream ARS = getClass().getResourceAsStream("/models/xml/Ars-2000-NF1-95-89.xml")) {
            return XMLModelParser.loadDiseaseCase(ARS)
                    .orElseThrow(() -> new Exception("Unable to read test data /models/xml/Ars-2000-NF1-95-89.xml"));
        }
    }
}