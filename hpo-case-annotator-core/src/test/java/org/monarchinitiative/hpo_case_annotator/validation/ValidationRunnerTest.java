package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.hpo_case_annotator.TestResources;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ValidationRunnerTest {


    /**
     * Tested instance.
     */
    private ValidationRunner runner;


    @Before
    public void setUp() throws Exception {
        CompletenessValidator completenessValidator = new CompletenessValidator();
        GenomicPositionValidator genomicPositionValidator = new GenomicPositionValidator(TestResources.TEST_REF_GENOME);
        ModelParser modelParser = new XMLModelParser(TestResources.TEST_MODEL_FILE_DIR);
        PubMedValidator pubMedValidator = new PubMedValidator(modelParser);
        runner = new ValidationRunner(genomicPositionValidator, completenessValidator, pubMedValidator);
    }


    /**
     * Test validation real-life model.
     */
    @Test
    public void testRunner() throws Exception {
        List<ValidationLine> validationLines = runner.validateModels(Collections.singleton(getArs()));
        assertEquals(2, validationLines.size());

        ValidationLine first = validationLines.get(0);
        assertEquals("Ars-2000-NF1", first.getModelName());
        assertEquals("CompletenessValidator", first.getValidatorName());
        assertEquals("PASSED", first.getValidationResult());
        assertEquals("All right!", first.getErrorMessage());

        ValidationLine second = validationLines.get(1);
        assertEquals("Ars-2000-NF1", second.getModelName());
        assertEquals("GenomicPositionValidator", second.getValidatorName());
        assertEquals("PASSED", second.getValidationResult());
        assertEquals("All right!", second.getErrorMessage());
    }


    /**
     * Utility method for creating DiseaseCaseModel instance for testing.
     *
     * @return {@link DiseaseCaseModel} instance for testing.
     */
    private DiseaseCaseModel getArs() throws Exception {
        try (FileInputStream ARS = new FileInputStream(new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml"))) {
            return XMLModelParser.loadDiseaseCaseModel(ARS);
        }
    }
}