package org.monarchinitiative.hpo_case_annotator.gui;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.application.HostServices;
import org.monarchinitiative.hpo_case_annotator.core.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.core.io.OMIMParser;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssembliesSerializer;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DiseaseCaseDataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValues;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.GuiElementValuesTest;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.ShowValidationResultsController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.MendelianVariantController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.SomaticVariantController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.SplicingVariantController;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.phenol.base.PhenolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Beans/dependencies required for testing are defined here. Last time it was not possible to define
 * {@link HostServices} bean.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class TestHpoCaseAnnotatorModule extends AbstractModule {

    private static final String PROPERTIES_FILE_NAME = "test-hca.properties";

    private static final String GENOME_ASSEMBLIES_FILE_NAME = "test-genome-assemblies.properties";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHpoCaseAnnotatorModule.class);


    @Override
    protected void configure() {
        bind(DiseaseCaseDataController.class);
//        bind(MainController.class);
        bind(MendelianVariantController.class);
        bind(SomaticVariantController.class);
        bind(SplicingVariantController.class);

        bind(ShowValidationResultsController.class);

        bind(ResourceBundle.class)
                .toInstance(ResourceBundle.getBundle(Main.class.getName()));

        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(1));

        bind(HostServicesWrapper.class)
                .toInstance(HostServicesWrapper.getInactiveWrapper()); // OK for testing

//        bind(ValidationRunner.class);
//        bind(GenomicPositionValidator.class);
//        bind(CompletenessValidator.class);
//        bind(PubMedValidator.class);
    }


    @Provides
    @Singleton
    public Properties properties() throws IOException {
        Properties properties = new Properties();
        try (InputStream is = TestHpoCaseAnnotatorModule.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME)) {
            properties.load(is);
        }
        return properties;
    }


    @Provides
    @Singleton
    public OptionalResources optionalResources() throws IOException, PhenolException, URISyntaxException {
        OptionalResources optionalResources = new OptionalResources();
//        final String file = TestHpoCaseAnnotatorModule.class.getResource("models").getFile();
//        final File diseaseCaseDir = new File(file);
//        optionalResources.setDiseaseCaseDir(diseaseCaseDir);

        // read Ontology
        try (InputStream is = TestHpoCaseAnnotatorModule.class.getResourceAsStream("/resource_files/HP.obo")) {
            optionalResources.setOntology(OptionalResources.deserializeOntology(is));
        }

        // read Entrez genes
        EntrezParser entrezParser = new EntrezParser(new File(TestHpoCaseAnnotatorModule.class.getResource("/resource_files/Homo_sapiens.gene_info.gz").getFile()));
        entrezParser.readFile();
        optionalResources.setEntrezId2gene(entrezParser.getEntrezMap());
        optionalResources.setEntrezId2symbol(entrezParser.getEntrezId2symbol());
        optionalResources.setSymbol2entrezId(entrezParser.getSymbol2entrezId());

        // read OMIM file
        try (InputStream is = TestHpoCaseAnnotatorModule.class.getResourceAsStream("/resource_files/omim.tsv")) {
            OMIMParser omimParser = new OMIMParser(is, Charset.forName("UTF-8"));
            optionalResources.setCanonicalName2mimid(omimParser.getCanonicalName2mimid());
            optionalResources.setMimid2canonicalName(omimParser.getMimid2canonicalName());
        }

        optionalResources.setBiocuratorId("HPO:walterwhite");
        return optionalResources;
    }


    @Provides
    @Singleton
    public GuiElementValues guiElementValues() throws IOException {
        try (InputStream is = GuiElementValuesTest.class.getResourceAsStream("test-gui-elements-values.yml")) {
            return GuiElementValues.guiElementValuesFrom(is);
        }
    }


    @Provides
    @Singleton
    public GenomeAssemblies genomeAssemblies(@Named("refGenomePropertiesFilePath") File refGenomePropertiesFilePath) throws IOException {

        if (refGenomePropertiesFilePath.isFile()) {
            try (InputStream is = new FileInputStream(refGenomePropertiesFilePath)) {
                LOGGER.debug("Loading genome assemblies from '{}'", refGenomePropertiesFilePath.getAbsolutePath());
                return GenomeAssembliesSerializer.deserialize(is);
            } catch (IOException ioe) {
                LOGGER.warn("Tried to load genome assembly definitions from '{}' but failed", refGenomePropertiesFilePath.getAbsolutePath());
                throw ioe;
            }
        } else {
            LOGGER.debug("No genome assemblies to be loaded");
            return new GenomeAssemblies();
        }
    }

    // ----------------------------------------- FILES -----------------------------------------------------------------


    /**
     * Get path to parent directory of the JAR file (or classes). Note, this is <em>NOT</em> the path to the JAR file.
     * The method ensures, that the parent directory is created, otherwise, the application will shut down.
     *
     * @return {@link File} path of the directory with code
     */
    @Provides
    @Named("codeHomeDir")
    public File codeHomeDir() throws IOException {
        File codeHomeDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile())
                .getParentFile();
        if (codeHomeDir.exists() || codeHomeDir.mkdirs()) {// ensure that the home dir exists
            LOGGER.trace("Code home directory is {}", codeHomeDir.getAbsolutePath());
            return codeHomeDir;
        } else {
            String msg = String.format("Cannot find or create code home directory here: '%s'",
                    codeHomeDir.getAbsolutePath());
            LOGGER.error(msg);
            throw new IOException(msg);
        }
    }

    @Provides
    @Named("refGenomePropertiesFilePath")
    public File refGenomePropertiesFilePath(@Named("appHomeDir") File appHomeDir) {
        return new File(appHomeDir, GENOME_ASSEMBLIES_FILE_NAME);
    }

    /**
     * @param appHomeDir path to application home directory, where HpoCaseAnnotator stores global settings and files. The path depends
     *                   on underlying operating system.
     * @return {@link File} pointing to location of application.properties file
     */
    @Provides
    @Named("propertiesFilePath")
    public File propertiesFilePath(@Named("appHomeDir") File appHomeDir) {
        return new File(appHomeDir, PROPERTIES_FILE_NAME);
    }


    /**
     * Get path to application home directory, where HpoCaseAnnotator stores global settings and files. The path depends
     * on underlying operating system.
     * <p>
     * A hidden <code>.hpo-case-annotator</code>  directory will be created in user's home dir in Linux and OSX.
     * <p>
     * App home directory for Windows' or unknown OS users will be the <code>HpoCaseAnnotator</code> directory in their
     * home dir.
     *
     * @return {@link File} with path to application home directory
     */
    @Provides
    @Named("appHomeDir")
    private File appHomeDir() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();

        File appHomeDir;
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) { // Unix
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".hpo-case-annotator");
        } else if (osName.contains("win")) { // Windows
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "HpoCaseAnnotator");
        } else if (osName.contains("mac")) { // OsX
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".hpo-case-annotator");
        } else { // unknown platform
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "HpoCaseAnnotator");
        }

        if (!appHomeDir.exists()) {
            LOGGER.debug("App home directory does not exist at {}", appHomeDir.getAbsolutePath());
            if (!appHomeDir.getParentFile().exists() && !appHomeDir.getParentFile().mkdirs()) {
                LOGGER.warn("Unable to create parent directory for app home at {}",
                        appHomeDir.getParentFile().getAbsolutePath());
                throw new IOException("Unable to create parent directory for app home at " +
                        appHomeDir.getParentFile().getAbsolutePath());
            } else {
                if (!appHomeDir.mkdir()) {
                    LOGGER.warn("Unable to create app home directory at {}", appHomeDir.getAbsolutePath());
                    throw new IOException("Unable to create app home directory at " + appHomeDir.getAbsolutePath());
                } else {
                    LOGGER.info("Created app home directory at {}", appHomeDir.getAbsolutePath());
                }
            }
        }
        return appHomeDir;
    }


    @Provides
    @Named("appNameVersion")
    private String appNameVersion(@Named("appName") String appName, @Named("appVersion") String appVersion) {
        return String.format("%s : %s", appName, appVersion);
    }


    @Provides
    @Named("appVersion")
    private String appVersion() {
        return "TEST";
    }


    @Provides
    @Named("appName")
    private String appName() {
        return "Hpo Case Annotator";
    }
}
