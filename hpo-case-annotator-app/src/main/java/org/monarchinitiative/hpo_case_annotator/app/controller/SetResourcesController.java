package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.app.config.HcaProperties;
import org.monarchinitiative.hpo_case_annotator.app.dialogs.Dialogs;
import org.monarchinitiative.hpo_case_annotator.app.io.GenomeAssemblyDownloader;
import org.monarchinitiative.hpo_case_annotator.app.model.*;
import org.monarchinitiative.hpo_case_annotator.app.ResourcePaths;
import org.monarchinitiative.hpo_case_annotator.app.io.Downloader;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResources;
import org.monarchinitiative.hpo_case_annotator.app.util.GenomicLocalResourceValidator;
import org.monarchinitiative.hpo_case_annotator.app.util.FileListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * This class is the controller of the setResources dialog. The user performs initial setup of the
 * resources that are required to run the GUI. The resource paths are stored in {@link OptionalResources} object. No
 * setting to {@link Properties} object is being done.
 * <p>
 * Created by Daniel Danis on 7/16/17.
 */
@Component
public class SetResourcesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetResourcesController.class);

    private final OptionalResources optionalResources;

    private final GenomicRemoteResources genomicRemoteResources;

    private final HcaProperties hcaProperties;

    private final File appHomeDir;

    private final ExecutorService executorService;

    @FXML
    private ProgressIndicator hg19ProgressIndicator;

    @FXML
    private Label hg19ProgressLabel;

    @FXML
    private ProgressIndicator hg38ProgressIndicator;

    @FXML
    private Label hg38ProgressLabel;

    @FXML
    private Label hg19JannovarLabel;

    @FXML
    private Label hg38JannovarLabel;

    @FXML
    private Label hg19GenomeLabel;
    @FXML
    private Label hg38GenomeLabel;

    @FXML
    private Label hpOboLabel;

    @FXML
    private ProgressIndicator hpoProgressIndicator;

    @FXML
    private Label curatedFilesDirLabel;

    @FXML
    private ListView<File> liftoverChainPathsListView;

    @FXML
    private TextField biocuratorIDTextField;

    public SetResourcesController(OptionalResources optionalResources,
                                  GenomicRemoteResources genomicRemoteResources,
                                  HcaProperties hcaProperties,
                                  File appHomeDir,
                                  ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.genomicRemoteResources = genomicRemoteResources;
        this.hcaProperties = hcaProperties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;

        initializeAppHomeDir();
    }

    private void initializeAppHomeDir() {
        File liftoverDir = appHomeDir.toPath().resolve(ResourcePaths.DEFAULT_LIFTOVER_FOLDER).toFile();
        if (!liftoverDir.isDirectory()) {
            if (!liftoverDir.mkdirs()) {
                LOGGER.warn("Unable to initialize directory for liftover chain files");
            }
        }
    }

    /**
     * Initialize elements of this controller.
     */
    @FXML
    private void initialize() {
        // reference genomes
        StringBinding hg19Path = Bindings.createStringBinding(() -> {
                    GenomicLocalResource hg19 = optionalResources.getGenomicLocalResources().getHg19();
                    if (hg19 == null) return "";

                    Path fasta = hg19.getFasta();
                    return fasta == null ? "" : fasta.toAbsolutePath().toString();
                },
                optionalResources.getGenomicLocalResources().hg19Property());
        hg19GenomeLabel.textProperty().bind(hg19Path);
        StringBinding hg38Path = Bindings.createStringBinding(() -> {
                    GenomicLocalResource hg38 = optionalResources.getGenomicLocalResources().getHg38();
                    if (hg38 == null) return "";
                    Path fasta = hg38.getFasta();
                    return fasta == null ? "" : fasta.toAbsolutePath().toString();
                },
                optionalResources.getGenomicLocalResources().hg38Property());
        hg38GenomeLabel.textProperty().bind(hg38Path);

        // Jannovar databases
        hg19JannovarLabel.textProperty().bind(optionalResources.getFunctionalAnnotationResources().hg19JannovarPathProperty().asString());
        hg38JannovarLabel.textProperty().bind(optionalResources.getFunctionalAnnotationResources().hg38JannovarPathProperty().asString());

        // HPO
        hpOboLabel.textProperty().bind(optionalResources.ontologyPathProperty().asString());

        // Curated files folder
        curatedFilesDirLabel.textProperty().bind(optionalResources.diseaseCaseDirProperty().asString());

        // Liftover chains
        liftoverChainPathsListView.setCellFactory(FileListCell.of());
        Bindings.bindContent(liftoverChainPathsListView.getItems(), optionalResources.liftoverChainFiles());

        // Biocurator
        biocuratorIDTextField.textProperty().bindBidirectional(optionalResources.biocuratorIdProperty());
    }

    @FXML
    private void downloadHg19RefGenomeButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Save hg19 fasta as");
        chooser.setInitialFileName("hg19.fa");
        File target = chooser.showSaveDialog(hg19ProgressIndicator.getScene().getWindow());

        Optional<GenomicLocalResource> paths = GenomicLocalResource.createFromFastaPath(target);
        paths.map(createDownloader(genomicRemoteResources.getHg19()))
                .map(dwn -> {
                    hg19ProgressIndicator.progressProperty().unbind();
                    hg19ProgressIndicator.progressProperty().bind(dwn.progressProperty());
                    hg19ProgressLabel.textProperty().unbind();
                    hg19ProgressLabel.textProperty().bind(dwn.messageProperty());

                    dwn.setOnSucceeded(event -> optionalResources.getGenomicLocalResources().setHg19(paths.get()));
                    dwn.setOnFailed(event -> optionalResources.getGenomicLocalResources().setHg19(null));
                    dwn.setOnCancelled(event -> optionalResources.getGenomicLocalResources().setHg19(null));
                    return dwn;
                })
                .ifPresent(executorService::submit);

    }

    private Function<GenomicLocalResource, GenomeAssemblyDownloader> createDownloader(GenomicRemoteResource resource) {
        return genomicAssemblyPaths -> new GenomeAssemblyDownloader(resource.genomeUrl(), resource.assemblyReportUrl(), genomicAssemblyPaths);
    }

    @FXML
    private void setPathToHg19ButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg19 FASTA file");
        chooser.setInitialFileName("hg19.fa");

        File fastaPath = chooser.showOpenDialog(hg19ProgressIndicator.getScene().getWindow());

        GenomicLocalResourceValidator validator = GenomicLocalResourceValidator.of(LOGGER::info);
        try {
            GenomicLocalResource.createFromFastaPath(fastaPath)
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg19()))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg19(resource));
        } catch (Exception ex) {
            Dialogs.showErrorDialog("Error", "Unable to use selected genome build", "See log for more details");
            LOGGER.error("Unable to use selected genome build: {}", ex.getMessage(), ex);
        }

    }

    @FXML
    private void downloadHg38RefGenomeButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Save hg38 fasta as");
        chooser.setInitialFileName("hg38.fa");
        File fastaPath = chooser.showSaveDialog(hg38ProgressIndicator.getScene().getWindow());

        Optional<GenomicLocalResource> paths = GenomicLocalResource.createFromFastaPath(fastaPath);
        paths.map(createDownloader(genomicRemoteResources.getHg38()))
                .map(dwn -> {
                    hg38ProgressIndicator.progressProperty().unbind();
                    hg38ProgressIndicator.progressProperty().bind(dwn.progressProperty());
                    hg38ProgressLabel.textProperty().unbind();
                    hg38ProgressLabel.textProperty().bind(dwn.messageProperty());

                    dwn.setOnSucceeded(event -> optionalResources.getGenomicLocalResources().setHg38(paths.get()));
                    dwn.setOnFailed(event -> optionalResources.getGenomicLocalResources().setHg38(null));
                    dwn.setOnCancelled(event -> optionalResources.getGenomicLocalResources().setHg38(null));
                    return dwn;
                })
                .ifPresent(executorService::submit);
    }

    @FXML
    private void setPathToHg38ButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg38 FASTA file");
        chooser.setInitialFileName("hg38.fa");

        File fastaPath = chooser.showOpenDialog(hg38ProgressIndicator.getScene().getWindow());

        GenomicLocalResourceValidator validator = GenomicLocalResourceValidator.of(LOGGER::info);

        try {
            GenomicLocalResource.createFromFastaPath(fastaPath)
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg38()))
                    .ifPresent(resource -> optionalResources.getGenomicLocalResources().setHg38(resource));
        } catch (Exception ex) {
            Dialogs.showErrorDialog("Error", "Unable to use selected genome build", "See log for more details");
            LOGGER.error("Unable to use selected genome build: {}", ex.getMessage(), ex);
        }
    }

    @FXML
    private void setPathToHg19JannovarButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select Jannovar transcript database for hg19");

        File databasePath = chooser.showOpenDialog(hg19JannovarLabel.getScene().getWindow());

        if (databasePath != null)
            optionalResources.getFunctionalAnnotationResources().setHg19JannovarPath(databasePath.toPath());
    }

    @FXML
    private void setPathToHg38JannovarButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select Jannovar transcript database for hg38");

        File databasePath = chooser.showOpenDialog(hg38JannovarLabel.getScene().getWindow());

        if (databasePath != null)
            optionalResources.getFunctionalAnnotationResources().setHg38JannovarPath(databasePath.toPath());
    }


    /**
     * Download HP.obo file to the data directory if the path to data directory has been set.
     */
    @FXML
    private void downloadHPOFileButtonAction(ActionEvent e) {
        e.consume();
        File target = new File(appHomeDir, ResourcePaths.DEFAULT_HPO_FILE_NAME);
        if (target.isFile()) {
            boolean overwrite = Dialogs.getBooleanFromUser("Download HPO",
                            "HPO file already exists at the target location",
                            "Overwrite?")
                    .map(bt -> bt.equals(ButtonType.OK))
                    .orElse(false);
            if (!overwrite) {
                try {
                    optionalResources.setOntologyPath(target);
                } catch (RuntimeException ex) {
                    LOGGER.warn("Error during opening the ontology file '{}'", target, ex);
                }
                return;
            }
        }
        String urlString = hcaProperties.hpo().ontologyUrl();
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            Dialogs.showWarningDialog("Download HPO obo file", "Error occurred", String.format("Malformed URL: %s", urlString));
            LOGGER.warn("Malformed URL: {}", urlString, ex);
            return;
        }
        Task<Void> task = new Downloader(url, target);

        hpoProgressIndicator.progressProperty().unbind();
        hpoProgressIndicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> {
            try {
                optionalResources.setOntologyPath(target);
            } catch (RuntimeException ex) {
                LOGGER.warn("Error occured during opening the ontology file '{}'", target, ex);
            }

        });
        task.setOnFailed(event -> {
            optionalResources.setOntologyPath(null);
            optionalResources.setOntology(null);
        });
        executorService.submit(task);
    }


    /**
     * Open DirChooser and ask user to provide a directory where curated files will be stored.
     */
    @FXML
    private void setCuratedDirButtonAction(ActionEvent e) {
        e.consume();
        File initial = optionalResources.getDiseaseCaseDir() != null && optionalResources.getDiseaseCaseDir().isDirectory()
                ? optionalResources.getDiseaseCaseDir()
                : new File(System.getProperty("user.home"));

        File curatedDir = Dialogs.selectDirectory((Stage) hg19ProgressIndicator.getScene().getWindow(), initial, "Set directory for curated files.");
        optionalResources.setDiseaseCaseDir(curatedDir);
    }

    @FXML
    private void downloadLiftoverChainFiles(ActionEvent e) {
        e.consume();
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(new URL(hcaProperties.liftover().hg18ToHg38Url()));
            urls.add(new URL(hcaProperties.liftover().hg19ToHg38Url()));
        } catch (MalformedURLException ex) {
            Dialogs.showWarningDialog("Download liftover chains", "Malformed URL", "See log for more details");
            LOGGER.warn("Malformed URL: {}", ex.getMessage());
            return;
        }
        Path liftoverFolder = appHomeDir.toPath().resolve(ResourcePaths.DEFAULT_LIFTOVER_FOLDER);
        for (URL url : urls) {
            String file = new File(url.getFile()).getName();
            Path target = liftoverFolder.resolve(file);

            Task<Void> task = new Downloader(url, target.toFile());
            task.setOnSucceeded(we -> optionalResources.liftoverChainFiles().add(target.toFile()));
            executorService.submit(task);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
