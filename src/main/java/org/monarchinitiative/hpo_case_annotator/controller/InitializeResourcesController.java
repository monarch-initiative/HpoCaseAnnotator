package org.monarchinitiative.hpo_case_annotator.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller of the initial resource setup dialog. The user is enforced to perform initial setup of
 * the resources that are required to run the GUI. The resource paths are stored using HRMDResourceManager.
 * Dialog can be closed only after initialization of all resources, otherwise the GUI exits. <p>Note: this controller is
 * NOT managed by Spring container. It should be used before creation of the ApplicationContext.<p>Created by Daniel
 * Danis on 7/16/17.
 */
@Deprecated
public final class InitializeResourcesController implements DialogController {

    private static final Logger log = LoggerFactory.getLogger(InitializeResourcesController.class);

//    private final URL entrezURL;
//
//    private final URL hpoURL;

    /**
     * Reference to the {@link FXMLDialog} representing the Stage(View) of the MVC.
     */
    private FXMLDialog dialog;

//    private HRMDResourceManager resourceManager;

    private OptionalResources resources;

    @FXML
    private TextField refGenomeTextField;

    @FXML
    private TextField hpOBOTextField;

    @FXML
    private TextField entrezGeneFileTextField;

    @FXML
    private TextField biocuratorIDTextField;

    @FXML
    private TextField dataDirectoryTextField;

    @FXML
    private TextField curatedDirTextField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label taskNameLabel;


//    public InitializeResourcesController(HRMDResourceManager resourceManager, URL entrezURL, URL hpoURL) {
//        this.resourceManager = resourceManager;
//        this.entrezURL = entrezURL;
//        this.hpoURL = hpoURL;
//    }


    /**
     * Validate content of OptionalResources and continue only if all resources had been initialized. Otherwise show warning
     * and don't let user pass.
     */
    @FXML
    void continueButtonAction() {
//        if (resourceManager.isInitialized()) {
//            resourceManager.saveResources();
//            dialog.close();
//        } else {
//            PopUps.showWarningDialog("Sorry", "Unable to continue", "Please initialize all resources.");
//        }
    }


    /**
     * Exit the GUI without setting anything.
     */
    @FXML
    void exitButtonAction() {
        dialog.close();
        System.exit(0);
    }


    /**
     * Open DirChooser and ask user to provide a directory where curated files will be stored.
     */
    @FXML
    void setCuratedDirButtonAction() {
//        Optional<File> curatedDir = ScreensConfig.selectDirectory(dialog, HRMDResourceManager.getUserHomeDir(), "Set " +
//                "directory for curated files.");
//        curatedDir.ifPresent(f -> resources.setDiseaseCaseDir(f.getAbsolutePath()));
    }


    /**
     * Open DirChooser and ask user to provide a directory where resources like HP.obo and Entrez gene file will be
     * stored.
     */
    @FXML
    void setDataDirectoryButtonAction() {
//        Optional<File> dataDir = ScreensConfig.selectDirectory(dialog, HRMDResourceManager.getUserHomeDir(), "Set data " +
//                "directory");
//        dataDir.ifPresent(f -> resources.setDataDir(f.getAbsolutePath()));
    }


    /**
     * Download entrez gene file to the data directory if the path to data directory has been set.
     */
    @FXML
    void downloadEntrezGenesButtonAction() {
//        if (!resourceManager.getDataDir().isPresent()) {
//            PopUps.showWarningDialog("Error", "Unable to download Entrez genes file", "Set path to data directory " +
//                    "first");
//            return;
//        }
//
//        File target = new File(resourceManager.getDataDir().get(), HRMDResourceManager.DEFAULT_ENTREZ_FILE_NAME);
//        Task<Void> task = new Downloader(entrezURL, target);
//        taskNameLabel.textProperty().bind(task.messageProperty());
//        progressBar.progressProperty().bind(task.progressProperty());
//        task.setOnSucceeded(e -> resources.setEntrezGenePath(target.getAbsolutePath()));
//        Thread dwnEntrez = new Thread(task);
//        dwnEntrez.setName("Download Entrez Thread");
//        dwnEntrez.setDaemon(true);
//        dwnEntrez.start();
    }


    /**
     * Download HP.obo file to the data directory if the path to data directory has been set.
     */
    @FXML
    void downloadHPOFileButtonAction() {
//        if (!resourceManager.getDataDir().isPresent()) {
//            PopUps.showWarningDialog("Error", "Unable to download HPO.obo file", "Set path to data directory " +
//                    "first");
//            return;
//        }
//        File target = new File(resourceManager.getDataDir().get(), HRMDResourceManager.DEFAULT_HPO_FILE_NAME);
//        Task<Void> task = new Downloader(hpoURL, target);
//        taskNameLabel.textProperty().bind(task.messageProperty());
//        progressBar.progressProperty().bind(task.progressProperty());
//        task.setOnSucceeded(e -> resources.setHpOBOPath(target.getAbsolutePath()));
//        Thread dwnHPO = new Thread(task);
//        dwnHPO.setName("Download HPO Thread");
//        dwnHPO.setDaemon(true);
//        dwnHPO.start();
    }


    /**
     * Set path to directory with reference genome files.
     */
    @FXML
    void setReferenceGenomeButtonAction() {
//        Optional<File> ref = ScreensConfig.selectDirectory(dialog, HRMDResourceManager.getUserHomeDir(), "Set " +
//                "path to directory with reference genome.");
//        ref.ifPresent(f -> resources.setRefGenomeDir(f.getAbsolutePath()));
    }


    /**
     * {@inheritDoc}
     *
     * @param location
     * @param resourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
//        resources = resourceManager.getResources();
//        dataDirectoryTextField.textProperty().bind(resources.dataDirProperty());
//        refGenomeTextField.textProperty().bind(resources.refGenomeDirProperty());
//        hpOBOTextField.textProperty().bind(resources.hpOBOPathProperty());
//        entrezGeneFileTextField.textProperty().bind(resources.entrezGenePathProperty());
//        curatedDirTextField.textProperty().bind(resources.diseaseCaseDirProperty());
//        biocuratorIDTextField.textProperty().bindBidirectional(resources.biocuratorIdProperty());
    }


    /**
     * {@inheritDoc}
     *
     * @param dialog The {@link FXMLDialog} instance which represents an independent window.
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

}
