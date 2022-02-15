package org.monarchinitiative.hpo_case_annotator.app;

public class ResourcePaths {

    public static final String PROPERTIES_FILE_NAME = "hpo-case-annotator.properties";


    /**
     * Use this name to save the Entrez gene file on the local filesystem.
     */
    public static final String DEFAULT_ENTREZ_FILE_NAME = "Homo_sapiens.gene_info.gz";

    public static final String DISEASE_CASE_DIR_PROPERTY = "disease.case.dir";

    public static final String BIOCURATOR_ID_PROPERTY = "biocurator.id";

    public static final String ONTOLOGY_PATH_PROPERTY = "hp.path";

    public static final String HG18_FASTA_PATH_PROPETY = "hg18.fasta.path";
    public static final String HG19_FASTA_PATH_PROPETY = "hg19.fasta.path";
    public static final String HG38_FASTA_PATH_PROPETY = "hg38.fasta.path";

    public static final String HG19_JANNOVAR_CACHE_PATH = "hg19.jannovar.path";
    public static final String HG38_JANNOVAR_CACHE_PATH = "hg38.jannovar.path";

    /**
     * Use this name to save HPO file on the local filesystem.
     */
    public static final String DEFAULT_HPO_FILE_NAME = "HP.json";

    public static final String ENTREZ_GENE_PATH_PROPERTY = "entrez.gene.path";

    public static final String LIFTOVER_CHAIN_PATHS_PROPERTY = "liftover.chain.paths";

    public static final String LIFTOVER_CHAIN_PATH_SEPARATOR = ";";

    public static final String DEFAULT_LIFTOVER_FOLDER = "liftover";

    private ResourcePaths() {
    }
}
