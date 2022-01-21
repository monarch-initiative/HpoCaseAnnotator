package org.monarchinitiative.hpo_case_annotator.gui;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import javafx.application.HostServices;
import org.monarchinitiative.hpo_case_annotator.core.liftover.LiftOverAdapter;
import org.monarchinitiative.hpo_case_annotator.core.reference.*;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.disease.DiseaseStatusController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.PedigreeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.individual.PedigreeMemberController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.BreakendController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfBreakendVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSequenceVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSymbolicVariantController;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.*;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.variant.*;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.svart.GenomicAssemblies;
import org.monarchinitiative.svart.GenomicAssembly;
import org.monarchinitiative.svart.GenomicRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
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

    private static final String GUI_ELEMENTS_VALUES = "gui-elements-values.yml";

    private final HostServices hostServices;


    HpoCaseAnnotatorModule(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    @Override
    protected void configure() {
        bind(HostServicesWrapper.class)
                .toInstance(HostServicesWrapper.wrap(hostServices));

        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        bind(ResourceBundle.class)
                .toInstance(ResourceBundle.getBundle(Main.class.getName()));

        bind(OptionalResources.class)
                .asEagerSingleton();

        bind(SetResourcesController.class).asEagerSingleton();
        bind(DiseaseCaseDataController.class);
        bind(MainController.class);
        bind(MendelianVariantController.class);
        bind(SomaticVariantController.class);
        bind(SplicingVariantController.class);
        bind(IntrachromosomalVariantController.class);
        bind(BreakpointVariantController.class);
        bind(LiftoverController.class);

        bind(FamilyStudyController.class);
        bind(PublicationController.class);

        // variant
        bind(BreakendController.class);

        // individual
        bind(PedigreeMemberController.class);
        bind(DiseaseStatusController.class);
        bind(PhenotypeEntryController.class);
        bind(OntologyTreeBrowserController.class);
        bind(PhenotypicFeatureController.class);
        bind(PhenotypicFeaturesTableController.class);

        // metadata
        bind(StudyMetadataController.class);
    }

    @Provides
    public VcfBreakendVariantController vcfBreakendVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfBreakendVariantController(genomicAssemblyRegistry);
    }

    @Provides
    public VcfSymbolicVariantController vcfSymbolicVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfSymbolicVariantController(genomicAssemblyRegistry);
    }

    @Provides
    public VcfSequenceVariantController vcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        return new VcfSequenceVariantController(genomicAssemblyRegistry);
    }

    @Provides
    public VariantSummaryController variantSummaryController(HCAControllerFactory hcaControllerFactory) {
        return new VariantSummaryController(hcaControllerFactory);
    }

    @Provides
    public PedigreeController pedigreeController(HCAControllerFactory hcaControllerFactory) {
        return new PedigreeController(hcaControllerFactory);
    }

    @Provides
    public PhenotypeBrowserController phenotypeBrowserController(HCAControllerFactory hcaControllerFactory) {
        return new PhenotypeBrowserController(hcaControllerFactory);
    }

    @Provides
    public DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry() {
        // TODO - get a real one
        GenomicAssemblyService hg19 = new GenomicAssemblyService() {
            @Override
            public GenomicAssembly genomicAssembly() {
                return GenomicAssemblies.GRCh37p13();
            }

            @Override
            public Optional<StrandedSequence> referenceSequence(GenomicRegion region) {
                return Optional.empty();
            }

            @Override
            public void close() throws Exception {

            }
        };
        GenomicAssemblyService hg38 = new GenomicAssemblyService() {
            @Override
            public GenomicAssembly genomicAssembly() {
                return GenomicAssemblies.GRCh38p13();
            }

            @Override
            public Optional<StrandedSequence> referenceSequence(GenomicRegion region) {
                return Optional.empty();
            }

            @Override
            public void close() throws Exception {

            }
        };

        List<GenomicAssemblyService> genomicAssemblyServices = List.of(hg19, hg38);
        return genomicAssemblyServices::stream;
    }

    @Provides
    public HCAControllerFactory hcaControllerFactory(Injector injector) {
        return injector::getInstance;
    }

    @Provides
    public LiftOverAdapter liftOverAdapter(@Named("liftoverDir") File liftoverDir) {
        LOGGER.warn("Creating Liftover adapter");
        return LiftOverAdapter.ofChainFolder(liftoverDir);
    }

    @Provides
    @Singleton
    @Named("scigraphMiningUrl")
    public URL scigraphMiningUrl(Properties properties) throws MalformedURLException {
        return new URL(Objects.requireNonNull(properties.getProperty("scigraph.mining.url")));
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
                URL propertiesUrl = Main.class.getResource("/" + PROPERTIES_FILE_NAME);
                LOGGER.info("Loading bundled app properties {}", propertiesUrl.getPath());
                properties.load(Main.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME));
            } catch (IOException e) {
                LOGGER.warn("Cannot load app properties", e);
                throw e;
            }
        }
        return properties;
    }


    /**
     * Figure out where YAML parameters file is localized. There are two possible paths:
     * <ul>
     * <li>in the directory where the JAR file is (<code>codeHomeDir</code>)</li>
     * <li>inside of the JAR file</li>
     * </ul>
     * File at the first path has a priority.
     * <p>
     * Use the YAML file to populate content of the {@link GuiElementValues}.
     *
     * @return {@link GuiElementValues} populated with content of the YAML file
     */
    @Provides
    @Singleton
    public GuiElementValues guiElementValues(@Named("codeHomeDir") File codeHomeDir) throws IOException {
        File target = new File(codeHomeDir, GUI_ELEMENTS_VALUES);
        if (target.isFile()) { // load from the file located next to the JAR file
            try (InputStream is = Files.newInputStream(target.toPath())) {
                LOGGER.info("Loading gui element values from file {}", target.getAbsolutePath());
                return GuiElementValues.guiElementValuesFrom(is);
            }
        } else { // try to load content from bundled file
            try (InputStream is = getClass().getResourceAsStream("/" + GUI_ELEMENTS_VALUES)) {
                LOGGER.info("Loading bundled gui element values file");
                return GuiElementValues.guiElementValuesFrom(is);
            }
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

    @Provides
    @Named("liftoverDir")
    @Singleton
    public File liftoverDir(@Named("appHomeDir") File appHomeDir) {
        return new File(appHomeDir, OptionalResources.DEFAULT_LIFTOVER_FOLDER);
    }

    /**
     * Get path to parent directory of the JAR file (or classes). Note, this is <em>NOT</em> the path to the JAR file.
     * The method ensures, that the parent directory is created, otherwise, the application will shut down.
     *
     * @return {@link File} path of the directory with code
     */
    @Provides
    @Named("codeHomeDir")
    @Singleton
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


    /**
     * @param appHomeDir path to application home directory, where HpoCaseAnnotator stores global settings and files. The path depends
     *                   on underlying operating system.
     * @return {@link File} pointing to location of application.properties file
     */
    @Provides
    @Named("propertiesFilePath")
    @Singleton
    public File propertiesFilePath(@Named("appHomeDir") File appHomeDir) {
        return new File(appHomeDir, PROPERTIES_FILE_NAME);
    }


    @Provides
    @Named("refGenomePropertiesFilePath")
    @Singleton
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
    @Singleton
    public File appHomeDir(@Named("appVersion") String appVersion) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        // we want to have one resource directory for release version and another for snapshots
        String suffix = appVersion.endsWith("-SNAPSHOT") ? "-SNAPSHOT" : "";

        File appHomeDir;
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) { // Unix
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".hpo-case-annotator" + suffix);
        } else if (osName.contains("win")) { // Windows
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "HpoCaseAnnotator" + suffix);
        } else if (osName.contains("mac")) { // OsX
            appHomeDir = new File(System.getProperty("user.home") + File.separator + ".hpo-case-annotator" + suffix);
        } else { // unknown platform
            appHomeDir = new File(System.getProperty("user.home") + File.separator + "HpoCaseAnnotator" + suffix);
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
    public String appNameVersion(@Named("appVersion") String appVersion, @Named("appName") String appName) {
        return String.format("%s : %s", appName, appVersion);
    }


    @Provides
    @Named("appVersion")
    public String appVersion() {
        // this property is set in Play#init()
        return System.getProperty(Main.HCA_VERSION_PROP_KEY);
    }

    @Provides
    @Named("appName")
    public String appName() {
        // this property is set in Play#init()
        return System.getProperty(Main.HCA_NAME_KEY);
    }
}
