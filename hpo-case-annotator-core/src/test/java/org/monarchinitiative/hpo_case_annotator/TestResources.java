package org.monarchinitiative.hpo_case_annotator;

import java.io.File;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class TestResources {

    public static final File TEST_REF_GENOME_FASTA = new File(TestResources.class.getResource("/org/monarchinitiative/hpo_case_annotator/refgenome/shortHg19.fa").getFile());

    public static final File TEST_MODEL_FILE_DIR = new File(TestResources.class.getResource("/models/xml").getFile());

    public static final File TEST_GPI_MODEL_FILE_DIR = new File(TestResources.class.getResource("/gpi_model").getFile());
}
