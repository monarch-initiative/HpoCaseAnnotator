package org.monarchinitiative.hpo_case_annotator.model;

import java.io.File;

public class TestResources {

    public static final File TEST_MODEL_FILE_DIR = new File(TestResources.class.getResource("/models/xml").getFile());

    public static final File TEST_GPI_MODEL_FILE_DIR = new File(TestResources.class.getResource("/gpi_model").getFile());


    private TestResources() {
        // private no-op
    }
}
