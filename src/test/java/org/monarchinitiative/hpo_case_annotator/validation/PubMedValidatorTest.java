package org.monarchinitiative.hpo_case_annotator.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.monarchinitiative.hpo_case_annotator.TestApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestApplicationConfig.class)
public class PubMedValidatorTest {

    /**
     * Tested instance.
     */
    @Autowired
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