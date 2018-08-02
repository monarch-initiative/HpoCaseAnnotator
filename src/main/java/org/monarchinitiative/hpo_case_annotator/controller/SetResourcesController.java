package org.monarchinitiative.hpo_case_annotator.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ontologizer.ontology.Ontology;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.io.Downloader;
import org.monarchinitiative.hpo_case_annotator.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.util.PopUps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * This class is the controller of the setResources dialog. The user performs initial setup of the
 * resources that are required to run the GUI. The resource paths are stored in {@link OptionalResources} object. No
 * setting to {@link Properties} object is being done.
 * <p>
 *     Created by Daniel Danis on 7/16/17.
 */
public final class SetResourcesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetResourcesController.class);

    private final OptionalResources optionalResources;

    private final Properties properties;

    private final File appHomeDir;

    private final ExecutorService executorService;

    @FXML
    public Label refGenomeLabel;

    @FXML
    public Label hpOboLabel;

    @FXML
    public Label entrezGeneLabel;

    @FXML
    public Label curatedFilesDirLabel;

    @FXML
    private TextField biocuratorIDTextField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label taskNameLabel;


    @Inject
    SetResourcesController(OptionalResources optionalResources, Properties properties, File appHomeDir, ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.properties = properties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;
    }


    /**
     * Open DirChooser and ask user to provide a directory where curated files will be stored.
     */
    @FXML
    void setCuratedDirButtonAction() {
        File curatedDir = PopUps.selectDirectory(new Stage(), new File(System.getProperty("user.home")), "Set " +
                "directory for curated files.");
        if (curatedDir != null) {
            optionalResources.setDiseaseCaseDir(curatedDir);
            curatedFilesDirLabel.setText(curatedDir.getAbsolutePath());
        } else {
            optionalResources.setDiseaseCaseDir(null);
            curatedFilesDirLabel.setText("unset");
        }
    }


    /**
     * Download entrez gene file to the app home directory.
     */
    @FXML
    void downloadEntrezGenesButtonAction() {
        File target = new File(appHomeDir, OptionalResources.DEFAULT_ENTREZ_FILE_NAME);
        if (target.isFile()) {
            boolean response = PopUps.getBooleanFromUser("Overwrite?", "Entrez file already exists at the target " +
                    "location", "Download Entrez gene file");
            if (!response) {
                EntrezParser parser = new EntrezParser(target.getAbsolutePath());
                optionalResources.setEntrezId2gene(parser.getEntrezMap());
                optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
                optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
                optionalResources.setEntrezPath(target);
                entrezGeneLabel.setText(target.getAbsolutePath());
                return;
            }
        }
        URL entrezGeneUrl;
        try {
            entrezGeneUrl = new URL(properties.getProperty("entrez.gene.url"));
        } catch (MalformedURLException e) {
            PopUps.showException("Download Entrez gene file", "Error occured", String.format("Malformed URL: %s",
                    properties.getProperty("entrez.gene.url")), e);
            LOGGER.error(String.format("Malformed URL: %s", properties.getProperty("entrez.gene.url")), e);
            return;
        }
        Task<Void> task = new Downloader(entrezGeneUrl, target);
        taskNameLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            EntrezParser parser = new EntrezParser(target.getAbsolutePath());
            optionalResources.setEntrezId2gene(parser.getEntrezMap());
            optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
            optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
            optionalResources.setEntrezPath(target);
            entrezGeneLabel.setText(target.getAbsolutePath());
        });
        task.setOnFailed(e -> {
            optionalResources.setEntrezId2gene(null);
            optionalResources.setEntrezId2symbol(null);
            optionalResources.setSymbol2entrezId(null);
            optionalResources.setEntrezPath(null);
            entrezGeneLabel.setText("unset");
        });
        executorService.submit(task);
    }


    /**
     * Download HP.obo file to the data directory if the path to data directory has been set.
     */
    @FXML
    void downloadHPOFileButtonAction() {
        File target = new File(appHomeDir, OptionalResources.DEFAULT_HPO_FILE_NAME);
        if (target.isFile()) {
            boolean response = PopUps.getBooleanFromUser("Overwrite?", "HPO file already exists at the target " +
                    "location", "Download " +
                    "HPO obo file");
            if (!response) {
                optionalResources.setOntology(OptionalResources.deserializeOntology(target));
                optionalResources.setOntologyPath(target);
                hpOboLabel.setText(target.getAbsolutePath());
                return;
            }
        }
        URL url;
        try {
            url = new URL(properties.getProperty("hp.obo.url"));
        } catch (MalformedURLException e) {
            PopUps.showException("Download HPO obo file", "Error occured", String.format("Malformed URL: %s",
                    properties.getProperty("hp.obo.url")), e);
            LOGGER.error(String.format("Malformed URL: %s", properties.getProperty("hp.obo.url")), e);
            return;
        }
        Task<Void> task = new Downloader(url, target);
        taskNameLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            optionalResources.setOntology(OptionalResources.deserializeOntology(target));
            optionalResources.setOntologyPath(target);
            hpOboLabel.setText(target.getAbsolutePath());
        });
        task.setOnFailed(e -> {
            optionalResources.setOntologyPath(null);
            optionalResources.setOntology(null);
            hpOboLabel.setText("unset");
        });
        executorService.submit(task);
    }


    /**
     * Set path to directory with reference genome files.
     */
    @FXML
    void setReferenceGenomeButtonAction() {
        File ref = PopUps.selectDirectory(new Stage(), new File(System.getProperty("user.home")), "Set " +
                "path to directory with reference genome.");
        if (ref != null) {
            optionalResources.setRefGenomeDir(ref);
            refGenomeLabel.setText(ref.getAbsolutePath());
        } else {
            optionalResources.setRefGenomeDir(null);
            refGenomeLabel.setText("unset");
        }
    }


    /**
     * Initialize elements of this controller.
     */
    public void initialize() {
//        refGenomeLabel.textProperty().bind(optionalResources.refGenomeDirProperty().asString());
        refGenomeLabel.setText((optionalResources.getRefGenomeDir() != null)
                ? optionalResources.getRefGenomeDir().getAbsolutePath()
                : "unset");
//        hpOboLabel.textProperty().bind(optionalResources.ontologyPathProperty().asString());
        hpOboLabel.setText((optionalResources.getOntologyPath() != null)
                ? optionalResources.getOntologyPath().getAbsolutePath()
                : "unset");
//        entrezGeneLabel.textProperty().bind(optionalResources.entrezPathProperty().asString());
        entrezGeneLabel.setText((optionalResources.getEntrezPath() != null)
                ? optionalResources.getEntrezPath().getAbsolutePath()
                : "unset");
//        curatedFilesDirLabel.textProperty().bind(optionalResources.diseaseCaseDirProperty().asString());
        curatedFilesDirLabel.setText((optionalResources.getDiseaseCaseDir() != null)
                ? optionalResources.getDiseaseCaseDir().getAbsolutePath()
                : "unset");
        biocuratorIDTextField.textProperty().bindBidirectional(optionalResources.biocuratorIdProperty());
    }

}
