package org.monarchinitiative.hpo_case_annotator.gui;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.DataController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.MainController;
import org.monarchinitiative.hpo_case_annotator.core.io.ChoiceBasket;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssembliesSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.1
 * @since 0.0
 */
public class HpoCaseAnnotatorModule extends AbstractModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(HpoCaseAnnotatorModule.class);

    private static final String PROPERTIES_FILE_NAME = "hpo-case-annotator.properties";

    private static final String GENOME_ASSEMBLIES_FILE_NAME = "genome-assemblies.properties";

    private static final String CHOICE_BASKET_FILE_NAME = "choice-basket.yml";

    /**
     * This is the {@link Stage} which is provided by JavaFX and registered into the Spring container in the
     * {@link Play#start(Stage)} method.
     */
    private final Stage primaryStage;

    private final HostServices hostServices;


    HpoCaseAnnotatorModule(Stage primaryStage, HostServices hostServices) {
        this.primaryStage = primaryStage;
        this.hostServices = hostServices;
    }


    @Override
    protected void configure() {
        bind(Stage.class)
                .annotatedWith(Names.named("primaryStage"))
                .toInstance(primaryStage);

        bind(HostServices.class)
                .toInstance(hostServices);

        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        bind(ResourceBundle.class)
                .toInstance(ResourceBundle.getBundle(Play.class.getName()));

        bind(OptionalResources.class)
                .asEagerSingleton();


        bind(DataController.class);
        bind(MainController.class);
    }


    /**
     * Return {@link Properties} with paths to resources. At first, {@link File} <code>propertiesFilePath</code>
     * will be tried. If the file doesn't exist, we will fall back to the <code>hpo-case-annotator.properties</code>
     * that is bundled in the JAR file.
     *
     * @param propertiesFilePath {@link File} pointing to the <code>hpo-case-annotator.properties</code> file
     * @return application {@link Properties}
     * @throws IOException if I/O error occurs
     */
    @Provides
    @Singleton
    public Properties properties(@Named("propertiesFilePath") File propertiesFilePath) throws IOException {
        Properties properties = new Properties();
        if (propertiesFilePath.isFile()) {
            try {
                LOGGER.info("Loading app properties from {}", propertiesFilePath.getAbsolutePath());
                properties.load(new FileReader(propertiesFilePath));
            } catch (IOException e) {
                LOGGER.warn("Cannot load app properties", e);
                throw e;
            }
        } else {
            try {
                URL propertiesUrl = Play.class.getResource("/" + PROPERTIES_FILE_NAME);
                LOGGER.info("Loading bundled app properties {}", propertiesUrl.getPath());
                properties.load(Play.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME));
            } catch (IOException e) {
                LOGGER.warn("Cannot load app properties", e);
                throw e;
            }
        }
        return properties;
    }


    /**
     * Figure out where YAML parameters file is localized. There are two possible paths: <ul> <li>in
     * the directory where the JAR file is (<code>codeHomeDir</code>)</li> <li>inside of the JAR file</li> </ul> The
     * first has a priority.
     *
     * Use the YAML file to populate content of the {@link ChoiceBasket}.
     *
     * @return {@link ChoiceBasket} populated with content of the YAML file
     */

    @Provides
    @Singleton
    public ChoiceBasket choiceBasket(@Named("codeHomeDir") File codeHomeDir) throws IOException {
        File target = new File(codeHomeDir, CHOICE_BASKET_FILE_NAME);
        if (target.isFile()) { // load from the file located next to the JAR file
            LOGGER.info("Loading choice basket from file {}", target.getAbsolutePath());
            return new ChoiceBasket(target);
        }
        try { // try to load content from bundled file
            URL url = getClass().getResource("/" + CHOICE_BASKET_FILE_NAME);
            LOGGER.info("Loading bundled choice basked from {}", url.toString());
            return new ChoiceBasket(url);
        } catch (IOException e) {
            LOGGER.warn("Tried to load bundled choice basket from but failed", e);
            throw e;
        }
    }

    @Provides
    @Singleton
    public GenomeAssemblies genomeAssemblies(@Named("refGenomePropertiesFilePath") File refGenomePropertiesFilePath) throws IOException {
        if (refGenomePropertiesFilePath.isFile()) {
            try (InputStream is = new FileInputStream(refGenomePropertiesFilePath)) {
                LOGGER.info("Loading genome assemblies from '{}'", refGenomePropertiesFilePath.getAbsolutePath());
                return GenomeAssembliesSerializer.deserialize(is);
            } catch (IOException ioe) {
                LOGGER.warn("Tried to load genome assembly definitions from '{}' but failed", refGenomePropertiesFilePath.getAbsolutePath());
                throw ioe;
            }
        } else {
            LOGGER.info("No genome assemblies to be loaded");
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


    @Provides
    @Named("refGenomePropertiesFilePath")
    public File refGenomePropertiesFilePath(@Named("appHomeDir") File appHomeDir) {
        return new File(appHomeDir, GENOME_ASSEMBLIES_FILE_NAME);
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
