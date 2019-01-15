package org.monarchinitiative.hpo_case_annotator.core.validation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// TODO - implement tests
@Ignore
public class PubMedValidatorTest {

    /**
     * Tested instance.
     */
    private PubMedValidator validator;


    @Before
    public void setUp() throws Exception {
//        File file = TestResources.TEST_MODEL_FILE_DIR;
//        ModelParser parser = new XMLModelParser(file);
//        validator = new PubMedValidator(parser);
    }


    /**
     * Test functionality of {@link PubMedValidator# seenThisPMIDBefore(String)} method. Test presence of pmid strings
     * of sample model files.
     */
    @Test
    public void testValidation() throws Exception {
        // some random funky input
//        assertFalse(validator.seenThisPMIDBefore("12345678948978854"));

        // these numbers are real PMIDs of model files located in resources/models/xml folder.
//        assertTrue(validator.seenThisPMIDBefore("10607834"));
//        assertTrue(validator.seenThisPMIDBefore("19375167"));
//        assertTrue(validator.seenThisPMIDBefore("21203346"));
//        assertTrue(validator.seenThisPMIDBefore("7514569"));
    }

}