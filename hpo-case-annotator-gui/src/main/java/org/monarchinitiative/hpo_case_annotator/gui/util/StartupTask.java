package org.monarchinitiative.hpo_case_annotator.gui.util;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.core.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.core.io.OMIMParser;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.Play;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Initialization of the GUI resources is being done here. Information from {@link Properties} parsed from
 * <code>hpo-case-annotator.properties</code> are being read and following resources are initialized:
 * <ul>
 * <li>Path to reference genome directory</li>
 * <li>Human phenotype ontology OBO file</li>
 * <li>Entrez gene file</li>
 * <li>Curated files directory</li>
 * <li>Biocurator ID</li>
 * <li>OMIM tab file</li>
 * </ul>
 * <p>
 * Changes made by user are stored for the next run in {@link Play#stop()} method.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public final class StartupTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupTask.class);

    private static final String OMIM_FILE_RESOURCE = "/dat/omim.tsv";

    private final OptionalResources optionalResources;

    private final Properties properties;

    private final GenomeAssemblies assemblies;


    public StartupTask(OptionalResources optionalResources, Properties properties, GenomeAssemblies assemblies) {
        this.optionalResources = optionalResources;
        this.properties = properties;
        this.assemblies = assemblies;
    }


    /**
     * Read {@link Properties} and initialize app resources in the {@link OptionalResources}:
     *
     * <ul>
     * <li>HPO ontology</li>
     * <li>Entrez gene file</li>
     * <li>Curated files directory</li>
     * <li>Biocurator ID, and </li>
     * <li>OMIM file</li>
     * </ul>
     *
     * @return nothing
     * @throws Exception if an error occurs
     */
    @Override
    protected Void call() throws Exception {
        // Curated files directory
        String curatedDirPath = properties.getProperty(OptionalResources.DISEASE_CASE_DIR_PROPERTY);
        if (curatedDirPath != null && new File(curatedDirPath).isDirectory()) {
            File curatedDir = new File(curatedDirPath);
            LOGGER.info("Setting curated files directory to '{}'", curatedDir.getAbsolutePath());
            optionalResources.setDiseaseCaseDir(curatedDir);
        } else
            LOGGER.info("Skipping setting of the curated files dictionary. Path '{}' does not point to directory",
                    curatedDirPath);

        // Biocurator ID
        optionalResources.setBiocuratorId(properties.getProperty(OptionalResources.BIOCURATOR_ID_PROPERTY, ""));

        // Then OMIM tsv file
        LOGGER.info("Parsing bundled OMIM file '{}'", OMIM_FILE_RESOURCE);
        try (InputStream is = getClass().getResourceAsStream(OMIM_FILE_RESOURCE)) {
            OMIMParser omimParser = new OMIMParser(is, Charset.forName("UTF-8"));
            optionalResources.setCanonicalName2mimid(omimParser.getCanonicalName2mimid());
            optionalResources.setMimid2canonicalName(omimParser.getMimid2canonicalName());
        }

        // Then Entrez file
        String entrezPath = properties.getProperty(OptionalResources.ENTREZ_GENE_PATH_PROPERTY);
        if (entrezPath != null && new File(entrezPath).isFile()) {
            File entrezGenesFile = new File(entrezPath);
            LOGGER.info("Loading Entrez genes from file '{}'", entrezGenesFile.getAbsolutePath());
            EntrezParser entrezParser = new EntrezParser(entrezGenesFile);
            entrezParser.readFile();
            optionalResources.setEntrezId2symbol(entrezParser.getEntrezId2symbol());
            optionalResources.setSymbol2entrezId(entrezParser.getSymbol2entrezId());
            optionalResources.setEntrezId2gene(entrezParser.getEntrezMap());
            optionalResources.setEntrezPath(entrezGenesFile);
        } else {
            LOGGER.info("Skipping loading Entrez genes since the location is unset");
        }

        // Finally HPO
        String ontologyPath = properties.getProperty(OptionalResources.ONTOLOGY_PATH_PROPERTY);
        if (ontologyPath != null && new File(ontologyPath).isFile()) {
            File ontologyFile = new File(ontologyPath);
            LOGGER.info("Loading HPO from file '{}'", ontologyFile.getAbsolutePath());
            optionalResources.setOntology(OptionalResources.deserializeOntology(ontologyFile));
            optionalResources.setOntologyPath(ontologyFile);
        } else {
            LOGGER.info("Skipping loading HPO file since the location is unset");
        }

        LOGGER.info("Done");
        return null;
    }
}
