package org.monarchinitiative.hpo_case_annotator.app.config;

import org.monarchinitiative.hpo_case_annotator.app.App;
import org.monarchinitiative.hpo_case_annotator.app.ResourcePaths;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Configuration
public class ResourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceConfiguration.class);

    private static final String GENOME_ASSEMBLIES_FILE_NAME = "genome-assemblies.properties";

    private final HcaProperties hcaProperties;

    public ResourceConfiguration(HcaProperties hcaProperties) {
        this.hcaProperties = hcaProperties;
    }

    @Bean
    public GenomicRemoteResources genomicRemoteResources() throws MalformedURLException {
        GenomicRemoteResourcesProperties reference = hcaProperties.reference();

        GenomicRemoteResourcesProperties.GenomicRemoteResource hg19Resources = reference.hg19();
        GenomicRemoteResource hg19 = new GenomicRemoteResource(new URL(hg19Resources.genomeFastaUrl()), new URL(hg19Resources.genomeAssemblyReportUrl()));
        GenomicRemoteResourcesProperties.GenomicRemoteResource hg38Resources = reference.hg38();
        GenomicRemoteResource hg38 = new GenomicRemoteResource(new URL(hg38Resources.genomeFastaUrl()), new URL(hg38Resources.genomeAssemblyReportUrl()));

        return new GenomicRemoteResources(hg19, hg38);
    }

    @Bean
    public File liftoverDir(File appHomeDir) {
        return new File(appHomeDir, ResourcePaths.DEFAULT_LIFTOVER_FOLDER);
    }

    /**
     * Get path to parent directory of the JAR file (or classes). Note, this is <em>NOT</em> the path to the JAR file.
     * The method ensures, that the parent directory is created, otherwise, the application will shut down.
     *
     * @return {@link File} path of the directory with code
     */
    @Bean
    public File codeHomeDir() throws IOException {
        File codeHomeDir = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getFile())
                .getParentFile();
        if (codeHomeDir.exists() || codeHomeDir.mkdirs()) {// ensure that the home dir exists
            LOGGER.debug("Code home directory is '{}'", codeHomeDir.getAbsolutePath());
            return codeHomeDir;
        } else {
            String msg = String.format("Cannot find or create code home directory at '%s'", codeHomeDir.getAbsolutePath());
            LOGGER.error(msg);
            throw new IOException(msg);
        }
    }

    /**
     * Return {@link Properties} with paths to resources. At first, {@link File} <code>propertiesFilePath</code>
     * will be tried. If the file doesn't exist, we will fall back to the <code>hpo-case-annotator.properties</code>
     * that is bundled in the JAR file.
     *
     * @param resourcePropertiesFilePath {@link File} pointing to the <code>hpo-case-annotator.properties</code> file
     * @return application {@link Properties}
     * @throws IOException if I/O error occurs
     */
    @Bean
    public Properties resourceProperties(File resourcePropertiesFilePath) throws IOException {
        Properties properties = new Properties();
        if (resourcePropertiesFilePath.isFile()) {
            try {
                LOGGER.info("Loading app properties from '{}'", resourcePropertiesFilePath.getAbsolutePath());
                properties.load(new FileReader(resourcePropertiesFilePath));
            } catch (IOException e) {
                LOGGER.warn("Cannot load app properties", e);
                throw e;
            }
        } else {
            try {
                URL propertiesUrl = MainConfiguration.class.getResource("/" + ResourcePaths.PROPERTIES_FILE_NAME);
                LOGGER.info("Loading bundled app properties '{}'", propertiesUrl);
                properties.load(MainConfiguration.class.getResourceAsStream("/" + ResourcePaths.PROPERTIES_FILE_NAME));
            } catch (IOException e) {
                LOGGER.warn("Cannot load app properties", e);
                throw e;
            }
        }
        return properties;
    }

    /**
     * @param appHomeDir path to application home directory, where HpoCaseAnnotator stores global settings and files. The path depends
     *                   on underlying operating system.
     * @return {@link File} pointing to location of application.properties file
     */
    @Bean
    public File resourcePropertiesFilePath(File appHomeDir) {
        return new File(appHomeDir, ResourcePaths.PROPERTIES_FILE_NAME);
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
    @Bean
    public File appHomeDir(String appVersion) throws IOException {
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
        LOGGER.info("Using '{}' as app home directory", appHomeDir.getAbsolutePath());
        return appHomeDir;
    }

    @Bean("appVersion")
    public String appVersion() {
        // this property is set in App#init()
        return hcaProperties.name();
    }

}
