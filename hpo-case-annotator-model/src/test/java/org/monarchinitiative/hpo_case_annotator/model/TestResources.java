package org.monarchinitiative.hpo_case_annotator.model;

import java.net.URL;

public class TestResources {

    public static final URL TEST_XML_MODEL_FILE_DIR = TestResources.class.getResource("/models/xml");

    public static final URL TEST_GPI_MODEL_FILE_DIR = TestResources.class.getResource("/gpi_model");


    private TestResources() {
        // private no-op
    }
}
