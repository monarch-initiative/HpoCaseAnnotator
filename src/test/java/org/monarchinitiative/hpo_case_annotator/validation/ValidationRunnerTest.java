package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.TestHpoCaseAnnotatorModule;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class ValidationRunnerTest {

    /**
     * Tested instance.
     */
    @Inject
    private ValidationRunner runner;


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
        File ARS = new File("target/test-classes/models/xml/Ars-2000-NF1-95-89.xml");
        Optional<DiseaseCaseModel> modelOptional = XMLModelParser.loadDiseaseCaseModel(ARS);
        if (modelOptional.isPresent())
            return modelOptional.get();
        throw new FileNotFoundException(String.format("Couldn't find model file for testing: %s", ARS.getAbsolutePath()));
    }
}