package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.beans.InvalidationListener;
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
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicLocalResources;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResource;
import org.monarchinitiative.hpo_case_annotator.app.model.genome.GenomicRemoteResources;
import org.monarchinitiative.hpo_case_annotator.app.util.GenomicLocalResourceValidator;
import org.monarchinitiative.hpo_case_annotator.app.util.FileListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
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
@Controller
public class SettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);
    private static final String UNSET = "Unset";

    private final OptionalResources optionalResources;

    private final GenomicRemoteResources genomicRemoteResources;

    private final HcaProperties hcaProperties;

    private final File codeHomeDir;
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
    private Label hpPathLabel;

    @FXML
    private ProgressIndicator hpoProgressIndicator;

    @FXML
    private Label curatedFilesDirLabel;

    @FXML
    private ListView<File> liftoverChainPathsListView;

    @FXML
    private TextField biocuratorIDTextField;

    public SettingsController(OptionalResources optionalResources,
                              GenomicRemoteResources genomicRemoteResources,
                              HcaProperties hcaProperties,
                              File codeHomeDir,
                              File appHomeDir,
                              ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.genomicRemoteResources = genomicRemoteResources;
        this.hcaProperties = hcaProperties;
        this.codeHomeDir = codeHomeDir;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;

        initializeAppHomeDir();
    }

    private void initializeAppHomeDir() {
        File liftoverDir = codeHomeDir.toPath().resolve(ResourcePaths.DEFAULT_LIFTOVER_FOLDER).toFile();
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
        // Reference genomes
        // Should never be null.
        GenomicLocalResources glr = optionalResources.getGenomicLocalResources();
        if (glr.getHg19() != null) {
            Path fasta = glr.getHg19().getFasta();
            if (fasta != null)
                hg19GenomeLabel.setText(fasta.toAbsolutePath().toString());
        }

        if (glr.getHg38() != null) {
            Path fasta = glr.getHg38().getFasta();
            if (fasta != null)
                hg38GenomeLabel.setText(fasta.toAbsolutePath().toString());
        }

        // Jannovar databases
        // Should never be null as well.
        FunctionalAnnotationResources far = optionalResources.getFunctionalAnnotationResources();
        if (far.getHg19JannovarPath() != null)
            hg19JannovarLabel.setText(far.getHg19JannovarPath().toAbsolutePath().toString());

        if (far.getHg38JannovarPath() != null)
            hg38JannovarLabel.setText(far.getHg38JannovarPath().toAbsolutePath().toString());

        // HPO
        if (optionalResources.getHpoPath() != null)
            hpPathLabel.setText(optionalResources.getHpoPath().toAbsolutePath().toString());

        // Curated files folder
        if (optionalResources.getDiseaseCaseDir() != null)
            curatedFilesDirLabel.setText(optionalResources.getDiseaseCaseDir().toAbsolutePath().toString());

        // Liftover chains
        liftoverChainPathsListView.setCellFactory(FileListCell.of());
        liftoverChainPathsListView.itemsProperty().bind(optionalResources.liftoverChainFilesProperty());

        // Biocurator
        biocuratorIDTextField.textProperty().bindBidirectional(optionalResources.biocuratorIdProperty());

        // At last ...
        InvalidationListener labelListener = updateLabelsIfChanged();
        optionalResources.getGenomicLocalResources().addListener(labelListener);
        optionalResources.getFunctionalAnnotationResources().addListener(labelListener);
        optionalResources.hpoPathProperty().addListener(labelListener);
        optionalResources.diseaseCaseDirProperty().addListener(labelListener);
    }

    private InvalidationListener updateLabelsIfChanged() {
        return obs -> {
            GenomicLocalResources glr = optionalResources.getGenomicLocalResources();
            FunctionalAnnotationResources far = optionalResources.getFunctionalAnnotationResources();
            if (obs.equals(glr.hg19Property())) {
                hg19GenomeLabel.setText(pathOrUnsetIfNull(glr.getHg19().getFasta()));
            } else if (obs.equals(glr.hg38Property())) {
                hg38GenomeLabel.setText(pathOrUnsetIfNull(glr.getHg38().getFasta()));
            } else if (obs.equals(far.hg19JannovarPathProperty())) {
                hg19JannovarLabel.setText(pathOrUnsetIfNull(far.getHg19JannovarPath()));
            } else if (obs.equals(far.hg38JannovarPathProperty())) {
                hg38JannovarLabel.setText(pathOrUnsetIfNull(far.getHg38JannovarPath()));
            } else if (obs.equals(optionalResources.hpoPathProperty())) {
                hpPathLabel.setText(pathOrUnsetIfNull(optionalResources.getHpoPath()));
            } else if (obs.equals(optionalResources.diseaseCaseDirProperty())) {
                curatedFilesDirLabel.setText(pathOrUnsetIfNull(optionalResources.getDiseaseCaseDir()));
            } else {
                System.err.printf("Changed: %s%n", obs);
            }
        };
    }

    private static String pathOrUnsetIfNull(Path path) {
        return path == null ? UNSET : path.toAbsolutePath().toString();
    }

    @FXML
    private void downloadHg19RefGenomeButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
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

        e.consume();
    }

    private Function<GenomicLocalResource, GenomeAssemblyDownloader> createDownloader(GenomicRemoteResource resource) {
        return genomicAssemblyPaths -> new GenomeAssemblyDownloader(resource.genomeUrl(), resource.assemblyReportUrl(), genomicAssemblyPaths);
    }

    @FXML
    private void setPathToHg19ButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
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

        e.consume();
    }

    @FXML
    private void downloadHg38RefGenomeButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
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

        e.consume();
    }

    @FXML
    private void setPathToHg38ButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
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

        e.consume();
    }

    @FXML
    private void setPathToHg19JannovarButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
        chooser.setTitle("Select Jannovar transcript database for hg19");

        File databasePath = chooser.showOpenDialog(hg19JannovarLabel.getScene().getWindow());

        if (databasePath != null)
            optionalResources.getFunctionalAnnotationResources().setHg19JannovarPath(databasePath.toPath());

        e.consume();
    }

    @FXML
    private void setPathToHg38JannovarButtonAction(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(codeHomeDir);
        chooser.setTitle("Select Jannovar transcript database for hg38");

        File databasePath = chooser.showOpenDialog(hg38JannovarLabel.getScene().getWindow());

        if (databasePath != null)
            optionalResources.getFunctionalAnnotationResources().setHg38JannovarPath(databasePath.toPath());

        e.consume();
    }


    /**
     * Download HP.obo file to the data directory if the path to data directory has been set.
     */
    @FXML
    private void downloadHPOFileButtonAction(ActionEvent e) {
        Path target = appHomeDir.toPath().resolve(ResourcePaths.DEFAULT_HPO_FILE_NAME);
        if (Files.isRegularFile(target)) {
            boolean overwrite = Dialogs.getBooleanFromUser("Download HPO",
                            "HPO file already exists at the target location",
                            "Overwrite?")
                    .map(bt -> bt.equals(ButtonType.OK))
                    .orElse(false);
            if (!overwrite) {
                optionalResources.setHpoPath(target);
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
        Task<Void> task = new Downloader(url, target.toFile());

        hpoProgressIndicator.progressProperty().unbind();
        hpoProgressIndicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> optionalResources.setHpoPath(target));
        task.setOnFailed(event -> optionalResources.setHpoPath(null));
        executorService.submit(task);

        e.consume();
    }


    @FXML
    private void downloadLiftoverChainFiles(ActionEvent e) {
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
            task.setOnSucceeded(we -> optionalResources.liftoverChainFilesProperty().add(target.toFile()));
            executorService.submit(task);
        }

        e.consume();
    }

    /**
     * Open DirChooser and ask user to provide a directory where curated files will be stored.
     */
    @FXML
    private void setCuratedDirButtonAction(ActionEvent e) {
        Path initial = optionalResources.getDiseaseCaseDir() != null && Files.isDirectory(optionalResources.getDiseaseCaseDir())
                ? optionalResources.getDiseaseCaseDir()
                : Path.of(System.getProperty("user.home"));

        Path curatedDir = Dialogs.selectDirectory((Stage) hg19ProgressIndicator.getScene().getWindow(), initial, "Set directory for curated files.");
        optionalResources.setDiseaseCaseDir(curatedDir);

        e.consume();
    }

}
