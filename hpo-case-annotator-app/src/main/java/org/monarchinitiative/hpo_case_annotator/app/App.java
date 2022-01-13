package org.monarchinitiative.hpo_case_annotator.app;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.app.config.*;
import org.monarchinitiative.hpo_case_annotator.app.controller.Main;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

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

    private ConfigurableApplicationContext context;

    private HostServices hostServices;

    @Override
    public void init() throws Exception {
        super.init();
        String[] args = getParameters().getRaw().toArray(String[]::new);
        context = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run(args);
        hostServices = getHostServices();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        loader.setControllerFactory(context::getBean);

        HcaProperties properties = context.getBean(HcaProperties.class);

        Parent parent = loader.load();
        Scene scene = new Scene(parent, 1000, 800);
        stage.setTitle(properties.name() + ' ' + properties.version());
        stage.setScene(scene);
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
            createReferenceGenomeFastaPath(localResources.getHg18())
                    .ifPresent(refGenomePath -> resourceProperties.setProperty(ResourcePaths.HG18_FASTA_PATH_PROPETY, refGenomePath));
            createReferenceGenomeFastaPath(localResources.getHg19())
                    .ifPresent(refGenomePath -> resourceProperties.setProperty(ResourcePaths.HG19_FASTA_PATH_PROPETY, refGenomePath));
            createReferenceGenomeFastaPath(localResources.getHg38())
                    .ifPresent(refGenomePath -> resourceProperties.setProperty(ResourcePaths.HG38_FASTA_PATH_PROPETY, refGenomePath));
        }

        if (optionalResources.getOntologyPath() != null) {
            resourceProperties.setProperty(ResourcePaths.ONTOLOGY_PATH_PROPERTY, optionalResources.getOntologyPath().getAbsolutePath());
        }
        if (optionalResources.getEntrezPath() != null) {
            resourceProperties.setProperty(ResourcePaths.ENTREZ_GENE_PATH_PROPERTY, optionalResources.getEntrezPath().getAbsolutePath());
        }
        if (optionalResources.getDiseaseCaseDir() != null) {
            resourceProperties.setProperty(ResourcePaths.DISEASE_CASE_DIR_PROPERTY, optionalResources.getDiseaseCaseDir().getAbsolutePath());
        }
        if (optionalResources.getBiocuratorId() != null) {
            resourceProperties.setProperty(ResourcePaths.BIOCURATOR_ID_PROPERTY, optionalResources.getBiocuratorId());
        }
        resourceProperties.store(new FileWriter(target), "Hpo Case Annotator properties");
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

    @Bean
    public HostServices hostServices() {
        return hostServices;
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
