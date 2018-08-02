package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.GuiceJUnitRunner;
import org.monarchinitiative.hpo_case_annotator.GuiceModules;
import org.monarchinitiative.hpo_case_annotator.TestHpoCaseAnnotatorModule;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestHpoCaseAnnotatorModule.class})
public class PubMedValidatorTest {

    /**
     * Tested instance.
     */
    @Inject
    private PubMedValidator validator;


    /**
     * Test functionality of {@link PubMedValidator#seenThisPMIDBefore(String)} method. Test presence of pmid strings
     * of sample model files.
     */
    @Test
    public void testValidation() throws Exception {
        // some random funky input
        assertFalse(validator.seenThisPMIDBefore("12345678948978854"));

        // these numbers are real PMIDs of model files located in resources/models/xml folder.
        assertTrue(validator.seenThisPMIDBefore("10607834"));
        assertTrue(validator.seenThisPMIDBefore("19375167"));
        assertTrue(validator.seenThisPMIDBefore("21203346"));
        assertTrue(validator.seenThisPMIDBefore("7514569"));
    }

}