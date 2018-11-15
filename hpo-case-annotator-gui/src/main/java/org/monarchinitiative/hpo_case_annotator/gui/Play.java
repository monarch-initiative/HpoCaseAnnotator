package org.monarchinitiative.hpo_case_annotator.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.gui.controllers.MainController;
import org.monarchinitiative.hpo_case_annotator.gui.hpotextmining.HpoTextMiningModule;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssembliesSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

/**
 * The driver class of the Hpo Case Annotator app.
 */
public class Play extends Application {

    private static final String WINDOW_TITLE = "Hpo Case Annotator";

    private static final Logger LOGGER = LoggerFactory.getLogger(Play.class);

    private Injector injector;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage window) throws Exception {
        Locale.setDefault(new Locale("en", "US"));

        // Apply CSS
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet("hpo-case-annotator.css");

        injector = Guice.createInjector(new HpoCaseAnnotatorModule(window, getHostServices()), new HpoTextMiningModule());
        ResourceBundle resourceBundle = injector.getInstance(ResourceBundle.class);

        Parent rootNode = FXMLLoader.load(MainController.class.getResource("MainView.fxml"), resourceBundle,
                new JavaFXBuilderFactory(), injector::getInstance);
        window.setTitle(WINDOW_TITLE);
        window.getIcons().add(new Image(getClass().getResourceAsStream("/img/app-icon.png")));
        window.setScene(new Scene(rootNode));
        window.show();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        injector.getInstance(ExecutorService.class).shutdown();

        // save properties
        OptionalResources optionalResources = injector.getInstance(OptionalResources.class); // singleton
        Properties properties = injector.getInstance(Properties.class);

        if (optionalResources.getOntologyPath() != null) {
            properties.setProperty(OptionalResources.ONTOLOGY_PATH_PROPERTY,
                    optionalResources.getOntologyPath().getAbsolutePath());
        }
        if (optionalResources.getEntrezPath() != null) {
            properties.setProperty(OptionalResources.ENTREZ_GENE_PATH_PROPERTY,
                    optionalResources.getEntrezPath().getAbsolutePath());
        }
        if (optionalResources.getDiseaseCaseDir() != null) {
            properties.setProperty(OptionalResources.DISEASE_CASE_DIR_PROPERTY,
                    optionalResources.getDiseaseCaseDir().getAbsolutePath());
        }
        if (optionalResources.getBiocuratorId() != null) {
            properties.setProperty(OptionalResources.BIOCURATOR_ID_PROPERTY,
                    optionalResources.getBiocuratorId());
        }
        File where = injector.getInstance(Key.get(File.class, Names.named("propertiesFilePath")));
        properties.store(new FileWriter(where), "Hpo Case Annotator properties");
        LOGGER.info("Properties saved to {}", where.getAbsolutePath());

        where = injector.getInstance(Key.get(File.class, Names.named("refGenomePropertiesFilePath")));
        GenomeAssemblies assemblies = injector.getInstance(GenomeAssemblies.class); // singleton
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(where))) {
            GenomeAssembliesSerializer.serialize(assemblies, os);
            LOGGER.info("Reference genome data configuration saved to {}", where.getAbsolutePath());
        }
    }

}
