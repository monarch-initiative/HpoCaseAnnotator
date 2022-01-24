package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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
import org.monarchinitiative.hpo_case_annotator.core.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyService;
import org.monarchinitiative.hpo_case_annotator.core.reference.InvalidFastaFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
    public ProgressIndicator hg19ProgressIndicator;

    @FXML
    public ProgressIndicator hg38ProgressIndicator;

    @FXML
    public ProgressIndicator hpoProgressIndicator;

    @FXML
    public ProgressIndicator entrezProgressIndicator;

    @FXML
    public Label hg19ProgressLabel;

    @FXML
    public Label hg38ProgressLabel;

    @FXML
    private HBox statusBar;

    @FXML
    private StatusBarController statusBarController;

    @FXML
    private Label hg38GenomeLabel;

    @FXML
    private Label hpOboLabel;

    @FXML
    private Label entrezGeneLabel;

    @FXML
    private Label curatedFilesDirLabel;

    @FXML
    private Label hg19GenomeLabel;

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
        hg19GenomeLabel.textProperty().bind(optionalResources.getGenomicLocalResources().getHg19().fastaProperty().asString());
        hg38GenomeLabel.textProperty().bind(optionalResources.getGenomicLocalResources().getHg38().fastaProperty().asString());

        hpOboLabel.textProperty().bind(optionalResources.ontologyPathProperty().asString());
        entrezGeneLabel.textProperty().bind(optionalResources.entrezPathProperty().asString());
        curatedFilesDirLabel.textProperty().bind(optionalResources.diseaseCaseDirProperty().asString());

        biocuratorIDTextField.textProperty().bindBidirectional(optionalResources.biocuratorIdProperty());
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

    /**
     * Download entrez gene file to the app home directory.
     */
    @FXML
    private void downloadEntrezGenesButtonAction(ActionEvent e) {
        e.consume();
        File target = new File(appHomeDir, ResourcePaths.DEFAULT_ENTREZ_FILE_NAME);
        if (target.isFile()) {
            boolean response = Dialogs.getBooleanFromUser("Download Entrez gene file",
                    "Entrez file already exists at the target location",
                    "Overwrite?")
                    .map(bt -> bt.equals(ButtonType.OK))
                    .orElse(false);
            if (!response) { // re-loading the old file
                try {
                    EntrezParser parser = new EntrezParser(target);
                    parser.readFile();
                    optionalResources.setEntrezId2gene(parser.getEntrezMap());
                    optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
                    optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
                    optionalResources.setEntrezPath(target);
                } catch (IOException ex) {
                    LOGGER.warn(ex.getMessage());
                    Dialogs.showException("Download Entrez gene file", "Error occurred", String.format("Error during parsing of Entrez gene file at '%s'",
                            target.getAbsolutePath()), ex);
                }
                return;
            }
        }
        String entrezGeneUrlString = hcaProperties.entrezGeneUrl();
        URL entrezGeneUrl;
        try {
            entrezGeneUrl = new URL(entrezGeneUrlString);
        } catch (MalformedURLException ex) {
            Dialogs.showException("Download Entrez gene file", "Error occured",
                    String.format("Malformed URL: %s", entrezGeneUrlString), ex);
            LOGGER.error(String.format("Malformed URL: %s", entrezGeneUrlString), ex);
            return;
        }

        Task<Void> task = new Downloader(entrezGeneUrl, target);
        entrezProgressIndicator.progressProperty().unbind();
        entrezProgressIndicator.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(event -> {
            try {
                EntrezParser parser = new EntrezParser(target);
                parser.readFile();
                optionalResources.setEntrezId2gene(parser.getEntrezMap());
                optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
                optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
                optionalResources.setEntrezPath(target);
            } catch (IOException ex) {
                LOGGER.warn(ex.getMessage());
                Dialogs.showException("Download Entrez gene file", "Error occured", String.format("Error during parsing of Entrez gene file at '%s'",
                        target.getAbsolutePath()), ex);
            }
        });
        task.setOnFailed(event -> {
            optionalResources.setEntrezId2gene(null);
            optionalResources.setEntrezId2symbol(null);
            optionalResources.setSymbol2entrezId(null);
            optionalResources.setEntrezPath(null);
        });
        executorService.submit(task);
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
            Dialogs.showException("Download HPO obo file", "Error occurred", String.format("Malformed URL: %s", urlString), ex);
            LOGGER.error("Malformed URL: {}", urlString, ex);
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
    private void setPathToHg19ButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg19 FASTA file");
        chooser.setInitialFileName("hg19.fa");

        File fastaPath = chooser.showOpenDialog(hg19ProgressIndicator.getScene().getWindow());

        GenomicLocalResourceValidator validator = GenomicLocalResourceValidator.of(statusBarController::showMessage);
        try {
            GenomicLocalResource.createFromFastaPath(fastaPath)
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg19()))
                    .ifPresent(optionalResources.getGenomicLocalResources()::setHg19);
        } catch (Exception ex) {
            Dialogs.showErrorDialog("Error", "Unable to use selected genome build", "See log for more details");
            LOGGER.error("Unable to use selected genome build: {}", ex.getMessage(), ex);
        }

    }

    @FXML
    private void setPathToHg38ButtonAction(ActionEvent e) {
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg38 FASTA file");
        chooser.setInitialFileName("hg38.fa");

        File fastaPath = chooser.showOpenDialog(hg38ProgressIndicator.getScene().getWindow());

        GenomicLocalResourceValidator validator = GenomicLocalResourceValidator.of(statusBarController::showMessage);

        try {
            GenomicLocalResource.createFromFastaPath(fastaPath)
                    .flatMap(local -> validator.verify(local, genomicRemoteResources.getHg38()))
                    .ifPresent(optionalResources.getGenomicLocalResources()::setHg38);
        } catch (Exception ex) {
            Dialogs.showErrorDialog("Error", "Unable to use selected genome build", "See log for more details");
            LOGGER.error("Unable to use selected genome build: {}", ex.getMessage(), ex);
        }
    }


    @FXML
    private void downloadLiftoverChainFiles(ActionEvent e) {
        e.consume();
        // TODO - add progress report & notification
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(new URL(hcaProperties.liftover().hg18ToHg38Url()));
            urls.add(new URL(hcaProperties.liftover().hg19ToHg38Url()));
        } catch (MalformedURLException ex) {
            Dialogs.showException("Download liftover chains", "Error occurred", "Malformed URL", ex);
            LOGGER.error("Malformed URL: {}", ex.getMessage());
            return;
        }
        for (URL url : urls) {
            String file = new File(url.getFile()).getName();
            Path target = appHomeDir.toPath().resolve(ResourcePaths.DEFAULT_LIFTOVER_FOLDER).resolve(file);

            Task<Void> task = new Downloader(url, target.toFile());
            executorService.submit(task);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static GenomicAssemblyService createGenomicAssemblyService(GenomicLocalResource resource) {
        try {
            return GenomicAssemblyService.of(resource.getAssemblyReport(), resource.getFasta(), resource.getFastaFai(), resource.getFastaDict());
        } catch (InvalidFastaFileException e) {
            Dialogs.showException("Error", "Error", "Unable to use downloaded genomic resources", e);
            return null;
        }
    }

    /**
     * @param assemblyPath must not be <code>null</code>, must be a file and the name must have the <em>.fa</em> suffix.
     * @return <code>true</code> if the {@link File} satisfies criteria stated above
     */
    private static boolean notNullAndValidFasta(File assemblyPath) {
        return assemblyPath != null && assemblyPath.isFile() && assemblyPath.getName().endsWith(".fa");
    }
}
