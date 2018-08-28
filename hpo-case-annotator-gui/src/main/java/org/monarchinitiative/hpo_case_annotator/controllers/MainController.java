package org.monarchinitiative.hpo_case_annotator.controllers;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.io.*;
import org.monarchinitiative.hpo_case_annotator.model.DiseaseCaseModel;
import org.monarchinitiative.hpo_case_annotator.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.util.StartupTask;
import org.monarchinitiative.hpo_case_annotator.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * This class is the controller of the main dialog of the HRMD app. <p>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final Stage primaryStage;

    private final Properties properties;

    private final File appHomeDir;

    private final DataController dataController;

    private final ResourceBundle resourceBundle;

    private final OptionalResources optionalResources;

    private final ExecutorService executorService;


    private HostServices hostServices;

    @FXML
    private ScrollPane contentScrollPane;


    @Inject
    public MainController(OptionalResources optionalResources, DataController dataController,
                          ResourceBundle resourceBundle, Stage primaryStage,
                          Properties properties, @Named("appHomeDir") File appHomeDir, ExecutorService executorService) {
        this.optionalResources = optionalResources;
        this.dataController = dataController;
        this.resourceBundle = resourceBundle;
        this.primaryStage = primaryStage;
        this.properties = properties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;
    }


    /**
     * Prepare runner using resources available at the moment.
     *
     * @param optionalResources with paths application resources
     * @return {@link ValidationRunner} usable to validate {@link DiseaseCaseModel}s
     */
    private static ValidationRunner getRunner(OptionalResources optionalResources) {
        GenomicPositionValidator genomicPositionValidator = new GenomicPositionValidator(optionalResources.getRefGenomeDir());
        CompletenessValidator completenessValidator = new CompletenessValidator();
        PubMedValidator pubMedValidator = new PubMedValidator(new XMLModelParser(optionalResources.getDiseaseCaseDir()));

        return new ValidationRunner(genomicPositionValidator, completenessValidator, pubMedValidator);
    }


    //    @Inject
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    @FXML
    void newMenuItemAction() {
        dataController.setModel(new DiseaseCaseModel());
        dataController.setCurrentModelPath(null);
    }


    @FXML
    void openMenuItemAction() {
        // we support opening of these files:
        final String SPLICING_XML = "XML data format (*.xml)";

        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setTitle("Select model to open");
        // the newest XML format which is used to save data
        FileChooser.ExtensionFilter xmlFileFormat =
                new FileChooser.ExtensionFilter(SPLICING_XML, "*.xml");
        filechooser.getExtensionFilters().add(xmlFileFormat);
        filechooser.setSelectedExtensionFilter(xmlFileFormat);
        File which = filechooser.showOpenDialog(primaryStage);

        if (which != null) {
            DiseaseCaseModel dcm;
            switch (filechooser.getSelectedExtensionFilter().getDescription()) {
                case SPLICING_XML:
                    try {
                        dcm = XMLModelParser.loadDiseaseCaseModel(new FileInputStream(which));
                    } catch (FileNotFoundException e) {
                        PopUps.showException("Open XML model", "Unable to decode XML file", "Did you set proper extension?", e);
                        return;
                    }
                    break;
                default:
                    throw new RuntimeException("This should not have had happened!");
            }

            dataController.setModel(dcm);
            dataController.setCurrentModelPath(which);
        }
    }


    @FXML
    void saveMenuItemAction() {
        saveModel(dataController.getCurrentModelPath(), dataController.getModel());
    }


    @FXML
    void saveAsMenuItemAction() {
        saveModel(null, dataController.getModel());
    }


    @FXML
    void exitMenuItemAction() {
        Platform.exit();
    }


    @FXML
    void showEditCurrentPublicationMenuItemAction() {
        try {
            ShowEditPublicationController controller = new ShowEditPublicationController(dataController.getModel().getPublication());

            Parent parent = FXMLLoader.load(
                    ShowEditPublicationController.class.getResource("ShowEditPublicationView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to show dialog for editing of the current publication", e);
            e.printStackTrace();
        }
    }


    @FXML
    void setResourcesMenuItemAction() {
        try {
            SetResourcesController controller = new SetResourcesController(optionalResources, properties, appHomeDir, executorService);
            Parent parent = FXMLLoader.load(
                    SetResourcesController.class.getResource("SetResourcesView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for setting resources", e);
            e.printStackTrace();
        }
    }


    @FXML
    void showCuratedPublicationsMenuItemAction() {
        File where = optionalResources.getDiseaseCaseDir();
        if (where == null || !where.isDirectory()) {
            PopUps.showInfoMessage("Set curated files directory first", "Unable to show curated publications");
            return;
        }
        File[] files = where.listFiles(f -> f.getName().endsWith(".xml"));
        if (files == null) {
            PopUps.showInfoMessage(String.format("No models in directory %s", where.getPath()), "Show curated publications");
            return;
        }
        Collection<DiseaseCaseModel> models = new ArrayList<>();
        for (File path : files) {
            try {
                models.add(XMLModelParser.loadDiseaseCaseModel(new FileInputStream(path)));
            } catch (FileNotFoundException e) {
                PopUps.showException("Open XML model", "Unable to decode XML file", "Did you set proper extension?", e);
            }
        }

        try {
            ShowPublicationsController controller = new ShowPublicationsController(hostServices);
            controller.setData(models);
            Parent parent = FXMLLoader.load(ShowPublicationsController.class.getResource("ShowPublicationsView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for showing curated publications", e);
            e.printStackTrace();
        }
    }


    @FXML
    void validateCurrentEntryMenuItemAction() {
        ValidationRunner validationRunner = getRunner(optionalResources);
        List<ValidationLine> lines = validationRunner.validateModels(
                Collections.singleton(dataController.getModel()));
        try {
            ShowValidationResultsController controller = new ShowValidationResultsController();
            Parent parent = FXMLLoader.load(ShowValidationResultsController.class.getResource("ShowValidationResultsView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setValidationLines(lines);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for validation of the current publication", e);
        }
    }


    @FXML
    void validateAllFilesMenuItemAction() {
        ValidationRunner validationRunner = getRunner(optionalResources);
        ModelParser modelParser = new XMLModelParser(optionalResources.getDiseaseCaseDir());
        Set<DiseaseCaseModel> models = new HashSet<>();
        for (File modelFile : modelParser.getModelNames()) {
            try (FileInputStream fis = new FileInputStream(modelFile)) {
                models.add(modelParser.readModel(fis));
            } catch (IOException e) {
                PopUps.showException("Open XML model", "Unable to decode XML file", "Did you set proper extension?", e);
            }
        }

        List<ValidationLine> lines = validationRunner.validateModels(models);

        try {
            ShowValidationResultsController controller = new ShowValidationResultsController();
            Parent parent = FXMLLoader.load(ShowValidationResultsController.class.getResource("ShowValidationResultsView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setValidationLines(lines);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for validation of the curated publications", e);
        }
    }


    @FXML
    void exportToCSVMenuItemAction() {
        ModelExporter exporter = new TSVModelExporter(optionalResources.getDiseaseCaseDir().getAbsolutePath(), "\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants to CSV file", "variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                exporter.exportModels(writer);
            } catch (IOException e) {
                LOGGER.warn(String.format("Error occured during variant export: %s", e.getMessage()), e);
            }
        }
    }


    @FXML
    void exportToSummaryFileMenuItemAction() {
        // TODO -- export to summary menuitem - implement me
    }


    @FXML
    void exportListOfComHetCasesWithOneCodingVariantMenuItemAction() {
        // TODO -- export list of comphets menuitem - implement me
    }


    @FXML
    void exportPhenopacketMenuItemAction() {
        String suggestedFileName = dataController.getModel().getFileName() + ".phenopacket";
        File where = PopUps.selectFileToSave(primaryStage,
                optionalResources.getDiseaseCaseDir(), "Save as Phenopacket", suggestedFileName);
        if (where != null) {
            PhenopacketExporter exporter = new PhenopacketExporter(where, dataController.getModel());
            exporter.writeToPhenopacket();
        }
    }


    @FXML
    void exportPhenoModelsMenuItemAction() {
        ModelExporter phenoExporter = new PhenoModelExporter(optionalResources.getDiseaseCaseDir().getAbsolutePath(), "\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants in format required by GPI project", "gpi_variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                phenoExporter.exportModels(writer);
            } catch (IOException e) {
                LOGGER.warn(String.format("Error occured during variant export: %s", e.getMessage()), e);
            }
        }
    }


    @FXML
    void helpMenuItemAction() {
        showHtmlContent(getClass().getResource("/html/hrmd.html"));
    }


    @FXML
    void TFListMenuItemAction() {
        showHtmlContent(getClass().getResource("/html/tflist.html"));
    }


    @FXML
    void NCIThesaurusMenuItemAction() {
        showHtmlContent(getClass().getResource("/html/ncithesaurus.html"));
    }


    private void showHtmlContent(URL contentUrl) {
        try {
            ShowHtmlContentController controller = new ShowHtmlContentController();
            Parent parent = FXMLLoader.load(ShowHtmlContentController.class.getResource("ShowHtmlContentView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setContent(contentUrl);
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display help dialog", e);
            e.printStackTrace();
        }
    }


    public void initialize() {
        StartupTask task = new StartupTask(optionalResources, properties);
        executorService.submit(task);
        try {
            Parent parent = FXMLLoader.load(DataController.class.getResource("DataView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> dataController);
            contentScrollPane.setContent(parent);
        } catch (IOException e) {
            LOGGER.warn(String.format("Error occured during initialization of the data view: %s", e.getMessage()));
            e.printStackTrace();
        }
    }


    /**
     * Save model to file in project's model directory.
     * <p>
     * <ul>
     *  <li>Ensure that DiseaseCaseDir exists</li>
     *  <li>Validate completness of model</li>
     *  <li>Check if we are saving model which has been opened from XML file or a new Model and ask user where
     *         to save, if necessary.</li>
     *  <li>Save it</li>
     * </ul>
     */
    private void saveModel(File currentModelPath, DiseaseCaseModel model) {
        ValidationRunner validationRunner = getRunner(optionalResources);
        String conversationTitle = "Save data into file";

        try {
            validationRunner.validateCompletness(model);
        } catch (ValidationException e) {
            boolean response = PopUps.getBooleanFromUser( "Do you want to save it anyway?",
                    "Incomplete data - " + e.getMessage(), "Validate data before saving");
            if (!response)
                return;
        }

        if (currentModelPath == null) {
            String suggestedXmlName = model.getFileName() + ".xml";
            File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(), conversationTitle,
                    suggestedXmlName);
            if (where == null) {
                return;
            }
            currentModelPath = where;
        }
        model.getBiocurator().setBioCuratorId(optionalResources.getBiocuratorId());
        try (FileOutputStream fos = new FileOutputStream(currentModelPath)) {
            XMLModelParser.saveDiseaseCaseModel(model, fos);
        } catch (IOException e) {
            PopUps.showException("Save data into file", "Unable to store data into file", "", e);
        }

        dataController.setCurrentModelPath(currentModelPath);
        PopUps.showInfoMessage(String.format("Data saved into file %s", currentModelPath.getName()), conversationTitle);
    }
}
