package org.monarchinitiative.hpo_case_annotator.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.monarchinitiative.hpo_case_annotator.app.config.*;
import org.monarchinitiative.hpo_case_annotator.app.dialogs.Dialogs;
import org.monarchinitiative.hpo_case_annotator.app.controller.Main;
import org.monarchinitiative.hpo_case_annotator.app.model.FunctionalAnnotationResources;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Include the following into the VM options when running HpoCaseAnnotator within an IDE:
 * <p>
 * <code>
 *   --add-reads org.monarchinitiative.hca.app=ALL-UNNAMED --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED --add-opens javafx.base/com.sun.javafx.logging=ALL-UNNAMED
 * </code>
 * </p>
 */
@SpringBootApplication
@EnableConfigurationProperties({
        HcaProperties.class,
        GenomicRemoteResourcesProperties.class,
        HpoProperties.class,
        TextMiningProperties.class,
        LiftoverProperties.class
})
public class App extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static final String BASE_CSS = Objects.requireNonNull(App.class.getResource("base.css")).toExternalForm();
    private static final int MAIN_WINDOW_WIDTH = 1600;
    private static final int MAIN_WINDOW_HEIGHT = 800;

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        super.init();
        String[] args = getParameters().getRaw().toArray(String[]::new);
        context = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        loader.setControllerFactory(context::getBean);

        HcaProperties properties = context.getBean(HcaProperties.class);
        context.getBean(HostServicesUrlBrowser.class).setHostServices(getHostServices());

        Scene scene = new Scene(loader.load(), MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        scene.getStylesheets().add(BASE_CSS);
        stage.setTitle(properties.name() + ' ' + properties.version());
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/img/donald-duck.png")));
        stage.setScene(scene);
        stage.setOnCloseRequest(quitUponUserConfirmation());
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        serializeResourceState(context.getBean(OptionalResources.class),
                context.getBean("resourceProperties", Properties.class),
                context.getBean("resourcePropertiesFilePath", File.class));

        LOGGER.debug("Shutting down...");
        context.close();
        LOGGER.info("Bye!");
    }

    private static void serializeResourceState(OptionalResources optionalResources, Properties resourceProperties, File target) throws IOException {
        GenomicLocalResources localResources = optionalResources.getGenomicLocalResources();
        if (localResources != null) {
            createReferenceGenomeFastaPath(localResources.getHg19())
                    .ifPresent(refGenomePath -> resourceProperties.setProperty(ResourcePaths.HG19_FASTA_PATH_PROPETY, refGenomePath));
            createReferenceGenomeFastaPath(localResources.getHg38())
                    .ifPresent(refGenomePath -> resourceProperties.setProperty(ResourcePaths.HG38_FASTA_PATH_PROPETY, refGenomePath));
        }

        FunctionalAnnotationResources functionalAnnotationResources = optionalResources.getFunctionalAnnotationResources();
        if (functionalAnnotationResources != null) {
            Path hg19JannovarPath = functionalAnnotationResources.getHg19JannovarPath();
            if (hg19JannovarPath != null)
                resourceProperties.setProperty(ResourcePaths.HG19_JANNOVAR_CACHE_PATH, hg19JannovarPath.toAbsolutePath().toString());
            Path hg38JannovarPath = functionalAnnotationResources.getHg38JannovarPath();
            if (hg38JannovarPath != null)
                resourceProperties.setProperty(ResourcePaths.HG38_JANNOVAR_CACHE_PATH, hg38JannovarPath.toAbsolutePath().toString());
        }

        if (optionalResources.getHpoPath() != null) {
            resourceProperties.setProperty(ResourcePaths.ONTOLOGY_PATH_PROPERTY, optionalResources.getHpoPath().toAbsolutePath().toString());
        }
        if (optionalResources.getDiseaseCaseDir() != null) {
            resourceProperties.setProperty(ResourcePaths.DISEASE_CASE_DIR_PROPERTY, optionalResources.getDiseaseCaseDir().toAbsolutePath().toString());
        }

        String liftoverChains = optionalResources.liftoverChainFilesProperty().stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(ResourcePaths.LIFTOVER_CHAIN_PATH_SEPARATOR));
        if (!liftoverChains.isBlank())
            resourceProperties.setProperty(ResourcePaths.LIFTOVER_CHAIN_PATHS_PROPERTY, liftoverChains);

        if (optionalResources.getBiocuratorId() != null) {
            resourceProperties.setProperty(ResourcePaths.BIOCURATOR_ID_PROPERTY, optionalResources.getBiocuratorId());
        }
        try (FileWriter writer = new FileWriter(target)) {
            resourceProperties.store(writer, "Hpo Case Annotator properties");
        }

        LOGGER.debug("Properties saved to `{}`", target.getAbsolutePath());
    }

    private static Optional<String> createReferenceGenomeFastaPath(GenomicLocalResource localResource) {
        if (localResource != null) {
            Path fasta = localResource.getFasta();
            if (fasta != null) {
                return Optional.of(fasta.toAbsolutePath().toString());
            }
        }
        return Optional.empty();
    }

    private static EventHandler<WindowEvent> quitUponUserConfirmation() {
        return e -> Dialogs.getBooleanFromUser("Exit", "Are you sure you want to quit?", null)
                .filter(buttonType -> buttonType.equals(ButtonType.CANCEL))
                .ifPresent(buttonType -> e.consume());
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
