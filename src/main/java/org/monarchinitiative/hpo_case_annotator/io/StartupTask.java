package org.monarchinitiative.hpo_case_annotator.io;

import javafx.concurrent.Task;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.Play;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.File;
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

    private final OptionalResources optionalResources;

    private final Properties properties;


    public StartupTask(OptionalResources optionalResources, Properties properties) {
        this.optionalResources = optionalResources;
        this.properties = properties;
    }


    @Override
    protected Void call() throws Exception {
        // Reference genome first
        String refGenomePath = properties.getProperty(OptionalResources.REF_GENOME_DIR_PROPERTY);
        if (refGenomePath!=null && new File(refGenomePath).isDirectory()) {
            File refGenome = new File(refGenomePath);
            LOGGER.info("Setting reference genome directory to {}", refGenome.getAbsolutePath());
            optionalResources.setRefGenomeDir(refGenome);
        } else
            LOGGER.info("Skipping setting of the reference genome dictionary. Path {} does not point to directory",
                    refGenomePath);
        // Then HPO
        String ontologyPath = properties.getProperty(OptionalResources.ONTOLOGY_PATH_PROPERTY);
        if (ontologyPath != null && new File(ontologyPath).isFile()) {
            File ontologyFile = new File(ontologyPath);
            LOGGER.info("Loading HPO from file {}", ontologyFile.getAbsolutePath());
            optionalResources.setOntology(OptionalResources.deserializeOntology(ontologyFile));
            optionalResources.setOntologyPath(ontologyFile);
        } else {
            LOGGER.info("Skipping loading HPO file since the location is unset");
        }
        // Then Entrez file
        String entrezPath = properties.getProperty(OptionalResources.ENTREZ_GENE_PATH_PROPERTY);
        if (entrezPath != null && new File(entrezPath).isFile()) {
            File entrezGenesFile = new File(entrezPath);
            LOGGER.info("Loading Entrez genes from file {}", entrezGenesFile.getAbsolutePath());
            EntrezParser entrezParser = new EntrezParser(entrezGenesFile.getAbsolutePath());
            optionalResources.setEntrezId2symbol(entrezParser.getEntrezId2symbol());
            optionalResources.setSymbol2entrezId(entrezParser.getSymbol2entrezId());
            optionalResources.setEntrezId2gene(entrezParser.getEntrezMap());
            optionalResources.setEntrezPath(entrezGenesFile);
        } else {
            LOGGER.info("Skipping loading Entrez genes since the locatino is unset");
        }
        // Curated files directory
        String curatedDirPath = properties.getProperty(OptionalResources.DISEASE_CASE_DIR_PROPERTY);
        if (curatedDirPath!=null && new File(curatedDirPath).isDirectory()) {
            File curatedDir = new File(curatedDirPath);
            LOGGER.info("Setting curated files directory to {}", curatedDir.getAbsolutePath());
            optionalResources.setDiseaseCaseDir(curatedDir);
        } else
            LOGGER.info("Skipping setting of the curated files dictionary. Path {} does not point to directory",
                    curatedDirPath);
        // Biocurator ID
        optionalResources.setBiocuratorId(properties.getProperty(OptionalResources.BIOCURATOR_ID_PROPERTY, ""));
        // Finally OMIM tab file. The file is bundled and therefore should be always present
        LOGGER.info("Parsing bundled OMIM file");
        OMIMParser omimParser = new OMIMParser(getClass().getResourceAsStream("/dat/omim.tsv")); // lil' hack
        optionalResources.setCanonicalName2mimid(omimParser.getCanonicalName2mimid());
        optionalResources.setMimid2canonicalName(omimParser.getMimid2canonicalName());
        LOGGER.info("Done");
        return null;
    }
}
