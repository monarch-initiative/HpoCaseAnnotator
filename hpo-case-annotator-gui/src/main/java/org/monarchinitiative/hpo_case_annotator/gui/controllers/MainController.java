package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.validation.CompletenessValidator;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationLine;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.gui.util.StartupTask;
import org.monarchinitiative.hpo_case_annotator.model.io.*;
import org.monarchinitiative.hpo_case_annotator.model.proto.Biocurator;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.ModelUtils;
import org.phenopackets.schema.v1.PhenoPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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

    private final GenomeAssemblies assemblies;

    private final HostServices hostServices;

    @FXML
    public StackPane contentStackPane;

    @FXML
    public MenuItem saveMenuItem;


    @Inject
    public MainController(OptionalResources optionalResources, DataController dataController,
                          ResourceBundle resourceBundle, Stage primaryStage,
                          Properties properties, @Named("appHomeDir") File appHomeDir, ExecutorService executorService,
                          GenomeAssemblies assemblies, HostServices hostServices) {
        this.optionalResources = optionalResources;
        this.dataController = dataController;
        this.resourceBundle = resourceBundle;
        this.primaryStage = primaryStage;
        this.properties = properties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;
        this.assemblies = assemblies;
        this.hostServices = hostServices;
    }


    @FXML
    void newMenuItemAction() {
        dataController.presentCase(DiseaseCase.getDefaultInstance());
        dataController.setCurrentModelPath(null);
    }


    @FXML
    void openMenuItemAction() {
        // we support opening of these files:
        final String SPLICING_XML = "XML data format (*.xml)";
        final String PROTO_JSON = "JSON data format (*.json)";

        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setTitle("Select model to open");
        // the newest XML format which is used to save data
        FileChooser.ExtensionFilter xmlFileFormat = new FileChooser.ExtensionFilter(SPLICING_XML, "*.xml");
        FileChooser.ExtensionFilter jsonFileFormat = new FileChooser.ExtensionFilter(PROTO_JSON, "*.json");
        filechooser.getExtensionFilters().addAll(xmlFileFormat, jsonFileFormat);
        filechooser.setSelectedExtensionFilter(jsonFileFormat);
        File which = filechooser.showOpenDialog(primaryStage);

        DiseaseCase diseaseCase;
        if (which != null) {
            switch (filechooser.getSelectedExtensionFilter().getDescription()) {
                case SPLICING_XML:
                    try (InputStream is = new BufferedInputStream(new FileInputStream(which))) {
                        diseaseCase = XMLModelParser.loadDiseaseCase(is);
                    } catch (IOException e) {
                        PopUps.showException("Open XML model", "Unable to decode XML file", "Did you set proper extension?", e);
                        return;
                    }
                    break;
                case PROTO_JSON:
                    try (InputStream is = new BufferedInputStream(new FileInputStream(which))) {
                        diseaseCase = ProtoJSONModelParser.readDiseaseCase(is);
                    } catch (FileNotFoundException e) {
                        PopUps.showException("Open JSON model", String.format("File '%s' not found", which.getAbsolutePath()), "", e);
                        return;
                    } catch (InvalidProtocolBufferException e) {
                        PopUps.showException("Open JSON model", String.format("Error parsing file content '%s'", which.getAbsolutePath()), "", e);
                        return;
                    } catch (IOException e) {
                        PopUps.showException("Open JSON model", String.format("Error while reading file '%s'", which.getAbsolutePath()), "", e);
                        return;
                    }
                    break;
                default:
                    throw new RuntimeException("This should not have had happened!");
            }

            dataController.presentCase(diseaseCase);
            dataController.setCurrentModelPath(which);
        }
    }


    @FXML
    void saveMenuItemAction() {
        saveModel(dataController.getCurrentModelPath(), dataController.getCase());
    }


    @FXML
    void saveAsMenuItemAction() {
        saveModel(null, dataController.getCase());
    }


    @FXML
    void exitMenuItemAction() {
        Platform.exit();
    }


    @FXML
    void showEditCurrentPublicationMenuItemAction() {
        try {
            ShowEditPublicationController controller = new ShowEditPublicationController(dataController.getCase().getPublication());

            Parent parent = FXMLLoader.load(
                    ShowEditPublicationController.class.getResource("ShowEditPublicationView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.setTitle("Edit metadata of the current publication");
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to show dialog for editing of the current publication", e);
        }
    }


    @FXML
    void setResourcesMenuItemAction() {
        try {
            SetResourcesController controller = new SetResourcesController(optionalResources, properties, appHomeDir,
                    executorService, primaryStage, assemblies);
            Parent parent = FXMLLoader.load(
                    SetResourcesController.class.getResource("SetResourcesView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.setTitle("Initialize Hpo Case Annotator resources");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
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
        Collection<DiseaseCase> models = new ArrayList<>();
        for (File path : files) {
            try {
                models.add(XMLModelParser.loadDiseaseCase(new FileInputStream(path)));
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
            stage.setTitle("Curated publications in '" + where.getAbsolutePath() + "'");
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
        // TODO - reject validation of incomplete model
        List<ValidationLine> lines = new ArrayList<>(ValidationRunner.validateModel(dataController.getCase(), assemblies));

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
    void validateAllFilesMenuItemAction() {
        // read all model files
        ModelParser modelParser = new XMLModelParser(optionalResources.getDiseaseCaseDir());
        List<DiseaseCase> models = new ArrayList<>();
        for (File modelFile : modelParser.getModelNames()) {
            try (FileInputStream fis = new FileInputStream(modelFile)) {
                models.add(modelParser.readModel(fis));
            } catch (IOException e) {
                PopUps.showException("Open XML model", "Unable to decode XML file", "Did you set proper extension?", e);
            }
        }

        List<ValidationLine> lines = models.stream()
                .map(m -> ValidationRunner.validateModel(m, assemblies))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        try {
            ShowValidationResultsController controller = new ShowValidationResultsController();
            Parent parent = FXMLLoader.load(ShowValidationResultsController.class.getResource("ShowValidationResultsView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setValidationLines(lines);
            Stage stage = new Stage();
            stage.setTitle("Validation results");
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for validation of the curated publications", e);
        }
    }


    @FXML
    void exportToCSVMenuItemAction() {
        final Optional<String> toggleChoiceFromUser = PopUps.getToggleChoiceFromUser(Arrays.asList("JSON", "XML"),
                String.format("What encoding is used for models in \n'%s'?", optionalResources.getDiseaseCaseDir().getAbsolutePath()),
                "Export as CSV");
        if (!toggleChoiceFromUser.isPresent()) {
            return;
        }

        String encoding = toggleChoiceFromUser.get();
        final ModelParser parser;
        switch (encoding) {
            case "JSON":
                parser = new ProtoJSONModelParser(optionalResources.getDiseaseCaseDir().toPath());
                break;
            case "XML":
                parser = new XMLModelParser(optionalResources.getDiseaseCaseDir());
                break;
            default:
                LOGGER.warn("Invalid choice '{}'", encoding);
                return;
        }

        // read all the cases
        Set<DiseaseCase> cases = new HashSet<>();
        for (File modelName : parser.getModelNames()) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(modelName))) {
                cases.add(parser.readModel(is));
            } catch (FileNotFoundException e) {
                LOGGER.warn("File '{}' not found", modelName, e);
            } catch (IOException e) {
                LOGGER.warn("Error reading file '{}'", modelName, e);
            }
        }

        ModelExporter exporter = new TSVModelExporter("\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants to CSV file", "variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                exporter.exportModels(cases, writer);
                LOGGER.info("Exported {} cases", cases.size());
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
        DiseaseCase diseaseCase = dataController.getCase();
        String suggestedFileName = ModelUtils.getNameFor(diseaseCase) + ".phenopacket";
        String title = "Save as Phenopacket";

        final FileChooser filechooser = new FileChooser();
        filechooser.setTitle(title);
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setInitialFileName(suggestedFileName);
        FileChooser.ExtensionFilter jsonFormat = new FileChooser.ExtensionFilter("Phenopacket in JSON format", "*.phenopacket");
        filechooser.getExtensionFilters().add(jsonFormat);
        filechooser.setSelectedExtensionFilter(jsonFormat);

        File where = filechooser.showSaveDialog(primaryStage);
        if (where != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(where.toPath())) {
                final PhenoPacket packet = PhenoPacketCodec.diseaseCaseToPhenopacket(diseaseCase);
                PhenoPacketCodec.writeAsPhenopacket(writer, packet);
            } catch (IOException e) {
                LOGGER.warn("Error occured during Phenopacket export", e);
                PopUps.showException(title, "Error occured during Phenopacket export", e.getMessage(), e);
            }
        }
    }


    @FXML
    void exportPhenoModelsMenuItemAction() {

        final Optional<String> toggleChoiceFromUser = PopUps.getToggleChoiceFromUser(Arrays.asList("JSON", "XML"),
                String.format("What encoding is used for models in \n'%s'?", optionalResources.getDiseaseCaseDir().getAbsolutePath()),
                "Export as CSV");
        if (!toggleChoiceFromUser.isPresent()) {
            return;
        }

        String encoding = toggleChoiceFromUser.get();
        final ModelParser parser;
        switch (encoding) {
            case "JSON":
                parser = new ProtoJSONModelParser(optionalResources.getDiseaseCaseDir().toPath());
                break;
            case "XML":
                parser = new XMLModelParser(optionalResources.getDiseaseCaseDir());
                break;
            default:
                LOGGER.warn("Invalid choice '{}'", encoding);
                return;
        }

        // read all the cases
        Set<DiseaseCase> cases = new HashSet<>();
        for (File modelName : parser.getModelNames()) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(modelName))) {
                cases.add(parser.readModel(is));
            } catch (FileNotFoundException e) {
                LOGGER.warn("File '{}' not found", modelName, e);
            } catch (IOException e) {
                LOGGER.warn("Error reading file '{}'", modelName, e);
            }
        }

        // write the cases to selected file
        ModelExporter phenoExporter = new PhenoModelExporter("\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants in format required by GPI project", "gpi_variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                phenoExporter.exportModels(cases, writer);
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
        StartupTask task = new StartupTask(optionalResources, properties, assemblies);
        executorService.submit(task);
        try {
            FXMLLoader loader = new FXMLLoader(DataController.class.getResource("DataView.fxml"));
            loader.setResources(resourceBundle);
            loader.setControllerFactory(clazz -> dataController);
            Parent parent = loader.load();

            contentStackPane.getChildren().add(parent);
        } catch (IOException e) {
            LOGGER.warn("Error occured during initialization of the data view.", e);
        }

        // disable for now
//        saveMenuItem.disableProperty().bind(dataController.diseaseCaseIsComplete().not());
    }


    /**
     * Save model to file in project's model directory.
     *
     * <ul>
     * <li>Ensure that DiseaseCaseDir exists</li>
     * <li>Validate completness of model</li>
     * <li>Check if we are saving model which has been opened from XML file or a new Model and ask user where
     * to save, if necessary.</li>
     * <li>Save it</li>
     * </ul>
     */
    private void saveModel(File currentModelPath, DiseaseCase model) {
        CompletenessValidator completenessValidator = new CompletenessValidator();
        completenessValidator.validateDiseaseCase(model);
        String conversationTitle = "Save data into file";


        if (currentModelPath == null) {
            FileChooser fileChooser = new FileChooser();

            final String SPLICING_XML = "XML data format (*.xml)";
            final String PROTO_JSON = "JSON data format (*.json)";
            FileChooser.ExtensionFilter xmlFileFormat = new FileChooser.ExtensionFilter(SPLICING_XML, "*.xml");
            FileChooser.ExtensionFilter jsonFileFormat = new FileChooser.ExtensionFilter(PROTO_JSON, "*.json");
            //        fileChooser.setSelectedExtensionFilter(jsonFileFormat);

            fileChooser.setTitle(conversationTitle);
            String suggestedName = ModelUtils.getNameFor(model) + ".json";
            fileChooser.setInitialFileName(suggestedName);
            fileChooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
            fileChooser.getExtensionFilters().addAll(jsonFileFormat, xmlFileFormat);
            File which = fileChooser.showSaveDialog(primaryStage);

            if (which == null) {
                return;
            }

            while (which.getName().matches(".*\\s.*")) { // at least one whitespace with or without surrounding characters
                if (PopUps.getBooleanFromUser("Provide new name or cancel", String.format("File name '%s' contains whitespace character", which.getName()), "Warning"))
                    which = fileChooser.showSaveDialog(primaryStage);
                else return;
            }
            currentModelPath = which;

            if (fileChooser.getSelectedExtensionFilter().getDescription().equals(jsonFileFormat.getDescription())) {
                try (OutputStream os = Files.newOutputStream(currentModelPath.toPath())) {
                    ProtoJSONModelParser.saveDiseaseCase(os, model, Charset.forName("UTF-8")); // TODO - charset is hardcoded
                } catch (IOException e) {
                    PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                    LOGGER.warn("Unable to store data into file {}", currentModelPath.getAbsolutePath(), e);
                    return;
                }
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().equals(xmlFileFormat.getDescription())) {
                try (FileOutputStream fos = new FileOutputStream(currentModelPath)) {
                    XMLModelParser.saveDiseaseCase(model, fos);
                } catch (IOException e) {
                    PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                    LOGGER.warn("Unable to store data into file {}", currentModelPath.getAbsolutePath(), e);
                    return;
                }
            }
        } else {
            // update the Biocurator ID
            model = model.toBuilder()
                    .setBiocurator(Biocurator.newBuilder().setBiocuratorId(optionalResources.getBiocuratorId())
                            .build())
                    .build();
            try (OutputStream os = Files.newOutputStream(currentModelPath.toPath())) {
                // save in the same format the model was saved
                if (currentModelPath.getName().endsWith(".xml")) {
                    XMLModelParser.saveDiseaseCase(model, os);
                } else if (currentModelPath.getName().endsWith(".json")) {
                    ProtoJSONModelParser.saveDiseaseCase(os, model, Charset.forName("UTF-8")); // TODO - charset is hardcoded
                }
            } catch (IOException e) {
                PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                LOGGER.warn("Unable to store data into file {}", currentModelPath.getAbsolutePath(), e);
                return;
            }
        }

        // SUCCESS (if we got here)
        dataController.setCurrentModelPath(currentModelPath);
        PopUps.showInfoMessage(String.format("Data saved into file %s", currentModelPath.getName()), conversationTitle);
    }
}
