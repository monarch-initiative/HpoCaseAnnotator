package org.monarchinitiative.hpo_case_annotator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javafx.application.HostServices;
import ontologizer.ontology.Ontology;
import org.monarchinitiative.hpo_case_annotator.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.controllers.MainController;
import org.monarchinitiative.hpo_case_annotator.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.OMIMParser;
import org.monarchinitiative.hpo_case_annotator.io.XMLModelParser;
import org.monarchinitiative.hpo_case_annotator.model.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.validation.CompletenessValidator;
import org.monarchinitiative.hpo_case_annotator.validation.GenomicPositionValidator;
import org.monarchinitiative.hpo_case_annotator.validation.PubMedValidator;
import org.monarchinitiative.hpo_case_annotator.validation.ValidationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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

    private static final String CHOICE_BASKET_FILE_NAME = "choice-basket.yml";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestHpoCaseAnnotatorModule.class);


    @Override
    protected void configure() {
        bind(DataController.class);
        bind(MainController.class);

        bind(ResourceBundle.class)
                .toInstance(ResourceBundle.getBundle(Play.class.getName()));

        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(1));

        bind(ModelParser.class)
                .to(XMLModelParser.class);


        bind(ValidationRunner.class);
        bind(GenomicPositionValidator.class);
        bind(CompletenessValidator.class);
        bind(PubMedValidator.class);

    }


    @Provides
    @Singleton
    public Properties properties() throws IOException {
        Properties properties = new Properties();

        properties.load(TestHpoCaseAnnotatorModule.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME));
        return properties;
    }


    @Provides
    @Singleton
    public OptionalResources optionalResources(Properties properties) throws FileNotFoundException {
        OptionalResources optionalResources = new OptionalResources();
        optionalResources.setRefGenomeDir(new File(properties.getProperty("test.genome.dir")));
        optionalResources.setDiseaseCaseDir(new File(properties.getProperty("test.xml.model.dir")));
        // Ontology
        Ontology ontology = OptionalResources.deserializeOntology(
                new File(properties.getProperty("test.hp.obo.path")));
        optionalResources.setOntology(ontology);
        // Entrez genes
        EntrezParser entrezParser = new EntrezParser(new File(properties.getProperty("test.entrez.file.path")));
        optionalResources.setEntrezId2gene(entrezParser.getEntrezMap());
        optionalResources.setEntrezId2symbol(entrezParser.getEntrezId2symbol());
        optionalResources.setSymbol2entrezId(entrezParser.getSymbol2entrezId());
        // OMIM file
        OMIMParser omimParser = new OMIMParser(new File(properties.getProperty("test.omim.file.path")));
        optionalResources.setCanonicalName2mimid(omimParser.getCanonicalName2mimid());
        optionalResources.setMimid2canonicalName(omimParser.getMimid2canonicalName());

        optionalResources.setBiocuratorId("HPO:walterwhite");
        return optionalResources;
    }


    @Provides
    @Singleton
    public ChoiceBasket choiceBasket(@Named("codeHomeDir") File codeHomeDir) throws IOException {
        File target = new File(codeHomeDir, CHOICE_BASKET_FILE_NAME);
        if (target.exists()) { // load from the file located next to the JAR file
            LOGGER.info("Loading choice basket from file {}", target.getAbsolutePath());
            return new ChoiceBasket(target);
        }

        try { // try to load content from bundled file
            URL url = getClass().getResource("/" + CHOICE_BASKET_FILE_NAME);
            return new ChoiceBasket(url);
        } catch (IOException e) {
            LOGGER.warn("Tried to load bundled choice basket from but failed", e);
            throw e;
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
        File codeHomeDir = new File(Play.class.getProtectionDomain().getCodeSource().getLocation().getFile())
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

}
