package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.core.io.Downloader;
import org.monarchinitiative.hpo_case_annotator.core.io.EntrezParser;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblyDownloader;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.model.proto.GenomeAssembly;
import org.monarchinitiative.phenol.base.PhenolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * This class is the controller of the setResources dialog. The user performs initial setup of the
 * resources that are required to run the GUI. The resource paths are stored in {@link OptionalResources} object. No
 * setting to {@link Properties} object is being done.
 * <p>
 * Created by Daniel Danis on 7/16/17.
 */
public final class SetResourcesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetResourcesController.class);

    private final OptionalResources optionalResources;

    private final Properties properties;

    private final File appHomeDir;

    private final ExecutorService executorService;

    private final Stage primaryStage;

    private final GenomeAssemblies assemblies;

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


    @Inject
    SetResourcesController(OptionalResources optionalResources, Properties properties, @Named("appHomeDir") File appHomeDir,
                           ExecutorService executorService, @Named("primaryStage") Stage primaryStage, GenomeAssemblies assemblies) {
        this.optionalResources = optionalResources;
        this.properties = properties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;
        this.primaryStage = primaryStage;
        this.assemblies = assemblies;
        initializeAppHomeDir();
    }

    private void initializeAppHomeDir() {
        File liftoverDir = appHomeDir.toPath().resolve(OptionalResources.DEFAULT_LIFTOVER_FOLDER).toFile();
        if (!liftoverDir.isDirectory()) {
            if (!liftoverDir.mkdirs()) {
                LOGGER.warn("Unable to initialize directory for liftover chain files");
            }
        }
    }


    /**
     * @param assemblyPath must not be <code>null</code>, must be a file and the name must have suffix <em>.fa</em>
     * @return <code>true</code> if the {@link File} satisfies criteria stated above
     */
    private static boolean notNullAndValidFasta(File assemblyPath) {
        return assemblyPath != null && assemblyPath.isFile() && assemblyPath.getName().endsWith(".fa");
    }


    /**
     * Open DirChooser and ask user to provide a directory where curated files will be stored.
     */
    @FXML
    void setCuratedDirButtonAction() {
        File initial = optionalResources.getDiseaseCaseDir() != null && optionalResources.getDiseaseCaseDir().isDirectory()
                ? optionalResources.getDiseaseCaseDir()
                : new File(System.getProperty("user.home"));

        File curatedDir = PopUps.selectDirectory(primaryStage, initial, "Set directory for curated files.");
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
            if (!response) { // re-loading the old file
                try {
                    EntrezParser parser = new EntrezParser(target);
                    parser.readFile();
                    optionalResources.setEntrezId2gene(parser.getEntrezMap());
                    optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
                    optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
                    optionalResources.setEntrezPath(target);
                    entrezGeneLabel.setText(target.getAbsolutePath());
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage());
                    PopUps.showException("Download Entrez gene file", "Error occurred", String.format("Error during parsing of Entrez gene file at '%s'",
                            target.getAbsolutePath()), e);
                }
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
        entrezProgressIndicator.progressProperty().unbind();
        entrezProgressIndicator.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            try {
                EntrezParser parser = new EntrezParser(target);
                parser.readFile();
                optionalResources.setEntrezId2gene(parser.getEntrezMap());
                optionalResources.setEntrezId2symbol(parser.getEntrezId2symbol());
                optionalResources.setSymbol2entrezId(parser.getSymbol2entrezId());
                optionalResources.setEntrezPath(target);
                entrezGeneLabel.setText(target.getAbsolutePath());
            } catch (IOException ex) {
                LOGGER.warn(ex.getMessage());
                PopUps.showException("Download Entrez gene file", "Error occured", String.format("Error during parsing of Entrez gene file at '%s'",
                        target.getAbsolutePath()), ex);
            }
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
                try {
                    optionalResources.setOntology(OptionalResources.deserializeOntology(target));
                    optionalResources.setOntologyPath(target);
                    hpOboLabel.setText(target.getAbsolutePath());
                } catch (IOException e) {
                    LOGGER.warn("Error during opening the ontology file '{}'", target, e);
                } catch (PhenolException e) {
                    LOGGER.warn("Error during parsing the ontology file '{}'", target, e);
                }
                return;
            }
        }
        URL url;
        try {
            url = new URL(properties.getProperty("hp.obo.url"));
        } catch (MalformedURLException e) {
            PopUps.showException("Download HPO obo file", "Error occurred", String.format("Malformed URL: %s",
                    properties.getProperty("hp.obo.url")), e);
            LOGGER.error(String.format("Malformed URL: %s", properties.getProperty("hp.obo.url")), e);
            return;
        }
        Task<Void> task = new Downloader(url, target);

        hpoProgressIndicator.progressProperty().unbind();
        hpoProgressIndicator.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e -> {
            try {
                optionalResources.setOntology(OptionalResources.deserializeOntology(target));
                optionalResources.setOntologyPath(target);
                hpOboLabel.setText(target.getAbsolutePath());
            } catch (IOException ex) {
                LOGGER.warn("Error occured during opening the ontology file '{}'", target, ex);
            } catch (PhenolException ex) {
                LOGGER.warn("Error during parsing the ontology file '{}'", target, ex);
            }

        });
        task.setOnFailed(e -> {
            optionalResources.setOntologyPath(null);
            optionalResources.setOntology(null);
            hpOboLabel.setText("unset");
        });
        executorService.submit(task);
    }


    /**
     * Initialize elements of this controller.
     */
    public void initialize() {
        if (assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_37)) {
            File hg19Assembly = assemblies.getAssemblyMap().get(GenomeAssembly.GRCH_37).toFile();
            if (notNullAndValidFasta(hg19Assembly)) {
                hg19GenomeLabel.setText(assemblies.getAssemblyMap().get(GenomeAssembly.GRCH_37).toFile().getAbsolutePath());
                hg19ProgressIndicator.setProgress(1);
            } else {
                LOGGER.info("Removing invalid path from hg19 build");
                assemblies.removeAssembly(GenomeAssembly.GRCH_37);
                hg19GenomeLabel.setText("");
                hg19ProgressIndicator.setProgress(0);
            }
        }

        if (assemblies.hasFastaForAssembly(GenomeAssembly.GRCH_38)) {
            File hg38Assembly = assemblies.getAssemblyMap().get(GenomeAssembly.GRCH_38).toFile();
            if (notNullAndValidFasta(hg38Assembly)) {
                hg38GenomeLabel.setText(assemblies.getAssemblyMap().get(GenomeAssembly.GRCH_38).toFile().getAbsolutePath());
                hg38ProgressIndicator.setProgress(1);
            } else {
                LOGGER.info("Removing invalid path from hg38 build");
                assemblies.removeAssembly(GenomeAssembly.GRCH_38);
                hg38GenomeLabel.setText("");
                hg38ProgressIndicator.setProgress(0);
            }
        }

        if (optionalResources.getOntologyPath() != null) {
            hpOboLabel.setText(optionalResources.getOntologyPath().getAbsolutePath());
            hpoProgressIndicator.setProgress(1);
        } else {
            hpOboLabel.setText("unset");
            hpoProgressIndicator.setProgress(0);
        }

        if (optionalResources.getEntrezPath() != null) {
            entrezGeneLabel.setText(optionalResources.getEntrezPath().getAbsolutePath());
            entrezProgressIndicator.setProgress(1);
        } else {
            entrezGeneLabel.setText("unset");
            entrezProgressIndicator.setProgress(0);
        }

        curatedFilesDirLabel.setText((optionalResources.getDiseaseCaseDir() != null)
                ? optionalResources.getDiseaseCaseDir().getAbsolutePath()
                : "unset");
        biocuratorIDTextField.textProperty().bindBidirectional(optionalResources.biocuratorIdProperty());
    }


    @FXML
    public void downloadHg19RefGenomeButtonAction() {
        try {
            URL url = new URL(properties.getProperty("hg19.chromfa.url"));
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(appHomeDir);
            chooser.setTitle("Save hg19 fasta as");
            chooser.setInitialFileName(GenomeAssembly.GRCH_37 + ".fa");
            File target = chooser.showSaveDialog(primaryStage);
            if (target == null) return;

            GenomeAssemblyDownloader downloader = new GenomeAssemblyDownloader(url, target);
            hg19ProgressIndicator.progressProperty().unbind();
            hg19ProgressIndicator.progressProperty().bind(downloader.progressProperty());
            hg19ProgressLabel.textProperty().unbind();
            hg19ProgressLabel.textProperty().bind(downloader.messageProperty());

            downloader.setOnSucceeded(e -> {
                hg19GenomeLabel.setText(target.getAbsolutePath());
                assemblies.putAssembly(GenomeAssembly.GRCH_37, target.toPath());

            });
            downloader.setOnFailed(e -> {
                hg19GenomeLabel.setText("");
                assemblies.removeAssembly(GenomeAssembly.GRCH_37);
            });
            downloader.setOnCancelled(e -> {
                hg19GenomeLabel.setText(e.getSource().getException().getMessage());
                assemblies.removeAssembly(GenomeAssembly.GRCH_37);
            });

            executorService.submit(downloader);
        } catch (MalformedURLException mue) {
            LOGGER.warn("Malformed url for downloading hg19 ref genome '{}'", properties.getProperty("hg19.chromfa.url"));
            PopUps.showException("Download hg19 reference genome", "Error",
                    "Malformed url for hg19 ref genome: " + properties.getProperty("hg19.chromfa.url"), mue);
        }
    }


    @FXML
    public void downloadHg38RefGenomeButtonAction() {
        try {
            URL url = new URL(properties.getProperty("hg38.chromfa.url"));
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(appHomeDir);
            chooser.setTitle("Save hg38 fasta as");
            chooser.setInitialFileName(GenomeAssembly.GRCH_38 + ".fa");
            File target = chooser.showSaveDialog(primaryStage);
            if (target == null) return;


            GenomeAssemblyDownloader downloader = new GenomeAssemblyDownloader(url, target);
            hg38ProgressIndicator.progressProperty().unbind();
            hg38ProgressIndicator.progressProperty().bind(downloader.progressProperty());
            hg38ProgressLabel.textProperty().unbind();
            hg38ProgressLabel.textProperty().bind(downloader.messageProperty());

            downloader.setOnSucceeded(e -> {
                hg38GenomeLabel.setText(target.getAbsolutePath());
                assemblies.putAssembly(GenomeAssembly.GRCH_38, target.toPath());
            });
            downloader.setOnFailed(e -> {
                hg38GenomeLabel.setText("");
                assemblies.removeAssembly(GenomeAssembly.GRCH_38);
            });
            downloader.setOnCancelled(e -> {
                hg38GenomeLabel.setText("");
                assemblies.removeAssembly(GenomeAssembly.GRCH_38);
            });

            executorService.submit(downloader);
        } catch (MalformedURLException mue) {
            LOGGER.warn("Malformed url for downloading hg19 ref genome '{}'", properties.getProperty("hg19.chromfa.url"));
            PopUps.showException("Download hg19 reference genome", "Error",
                    "Malformed url for hg19 ref genome: " + properties.getProperty("hg19.chromfa.url"), mue);
        }
    }


    @FXML
    public void setPathToHg19ButtonAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg19 FASTA file");
        chooser.setInitialFileName(GenomeAssembly.GRCH_37 + ".fa");

        while (true) { // loop until we get proper FASTA file
            File target = chooser.showOpenDialog(primaryStage);
            if (target == null) break;
            else if (!target.isFile()) { // we need to get path to a file
                PopUps.showInfoMessage("Provide path to valid FASTA file", String.format("'%s' - not a file", target.getAbsolutePath()));
            } else if (!target.getName().endsWith(".fa")) { // and the file needs to have proper suffix
                PopUps.showInfoMessage("Provide path to file that has '*.fa' suffix", String.format("'%s' has a bad suffix", target.getName()));
            } else { // all set, set path and break
                hg19GenomeLabel.setText(target.getAbsolutePath());
                assemblies.putAssembly(GenomeAssembly.GRCH_37, target.toPath());
                hg19ProgressIndicator.setProgress(1);
                break;
            }
        }
    }


    @FXML
    public void setPathToHg38ButtonAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(appHomeDir);
        chooser.setTitle("Select local hg38 FASTA file");
        chooser.setInitialFileName(GenomeAssembly.GRCH_38.toString() + ".fa");

        while (true) { // loop until we get proper FASTA file
            File target = chooser.showOpenDialog(primaryStage);
            if (target == null) break;
            else if (!target.isFile()) { // we need to get path to a file
                PopUps.showInfoMessage("Provide path to valid FASTA file", String.format("'%s' - not a file", target.getAbsolutePath()));
            } else if (!target.getName().endsWith(".fa")) { // and the file needs to have proper suffix
                PopUps.showInfoMessage("Provide path to file that has '*.fa' suffix", String.format("'%s' has a bad suffix", target.getName()));
            } else { // all set, set path and break
                hg38GenomeLabel.setText(target.getAbsolutePath());
                assemblies.putAssembly(GenomeAssembly.GRCH_38, target.toPath());
                hg38ProgressIndicator.setProgress(1);
                break;
            }
        }
    }

    @FXML
    public void downloadLiftoverChainFiles() {
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(new URL(properties.getProperty("hg18.hg38.chain.url")));
            urls.add(new URL(properties.getProperty("hg19.hg38.chain.url")));
        } catch (MalformedURLException e) {
            PopUps.showException("Download liftover chains", "Error occurred", "Malformed URL", e);
            LOGGER.error("Malformed URL: {}", e.getMessage());
            return;
        }
        for (URL url : urls) {
            String file = new File(url.getFile()).getName();
            Path target = appHomeDir.toPath().resolve(OptionalResources.DEFAULT_LIFTOVER_FOLDER).resolve(file);

            Task<Void> task = new Downloader(url, target.toFile());
            executorService.submit(task);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
