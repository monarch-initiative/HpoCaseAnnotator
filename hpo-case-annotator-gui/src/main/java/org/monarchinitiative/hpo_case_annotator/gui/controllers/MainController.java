package org.monarchinitiative.hpo_case_annotator.gui.controllers;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.monarchinitiative.hpo_case_annotator.core.refgenome.GenomeAssemblies;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationResult;
import org.monarchinitiative.hpo_case_annotator.core.validation.ValidationRunner;
import org.monarchinitiative.hpo_case_annotator.gui.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.gui.util.HostServicesWrapper;
import org.monarchinitiative.hpo_case_annotator.gui.util.PopUps;
import org.monarchinitiative.hpo_case_annotator.gui.util.StartupTask;
import org.monarchinitiative.hpo_case_annotator.model.codecs.Codecs;
import org.monarchinitiative.hpo_case_annotator.model.codecs.DiseaseCaseToBassPhenopacketCodec;
import org.monarchinitiative.hpo_case_annotator.model.codecs.DiseaseCaseToPhenopacketCodec;
import org.monarchinitiative.hpo_case_annotator.model.io.*;
import org.monarchinitiative.hpo_case_annotator.model.proto.Biocurator;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.proto.Publication;
import org.monarchinitiative.hpo_case_annotator.model.utils.ModelUtils;
import org.phenopackets.schema.v1.Phenopacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is the controller of the main dialog of the HpoCaseAnnotator app. <p>
 * The main dialog displays menu and a tab pane. Each case is presented in a separate tab.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final JsonFormat.Printer PRINTER = JsonFormat.printer();

    private final OptionalResources optionalResources;

    private final ResourceBundle resourceBundle;

    private final Injector injector;

    private final Stage primaryStage;

    private final Properties properties;

    private final File appHomeDir;

    private final ExecutorService executorService;

    private final GenomeAssemblies assemblies;

    private final HostServicesWrapper hostServices;

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public MenuItem showCuratedPublicationsMenuItem;

    @FXML
    public MenuItem saveAsMenuItem;

    @FXML
    public MenuItem saveAllMenuItem;

    @FXML
    public TabPane contentTabPane;

    @FXML
    public MenuItem validateCurrentEntryMenuItem;

    @FXML
    public MenuItem exportPhenopacketMenuItem;

    /**
     * This list contains controllers of the tabs in the same order as they are present in the {@link #contentTabPane#getTabs} method.
     * <p>
     * The only place where items are added into this list is the {@link #addTab(DiseaseCase, Path)} method.
     */
    private List<DiseaseCaseDataController> controllers = new ArrayList<>();


    @Inject
    public MainController(OptionalResources optionalResources, ResourceBundle resourceBundle, Injector injector, Stage primaryStage,
                          Properties properties, @Named("appHomeDir") File appHomeDir, ExecutorService executorService,
                          GenomeAssemblies assemblies, HostServicesWrapper hostServices) {
        this.optionalResources = optionalResources;
        this.resourceBundle = resourceBundle;
        this.injector = injector;
        this.primaryStage = primaryStage;
        this.properties = properties;
        this.appHomeDir = appHomeDir;
        this.executorService = executorService;
        this.assemblies = assemblies;
        this.hostServices = hostServices;
    }

    /**
     * @param modelDir a {@link File} representing directory with model files.
     * @return {@link Codecs.SupportedDiseaseCaseFormat} to be used further or <code>null</code>
     */
    private static Codecs.SupportedDiseaseCaseFormat figureOutWhichFormatToUse(File modelDir) {
        File[] files = modelDir.listFiles();
        if (files == null) {
            return null;
        }

        final List<String> fileNameList = Arrays.stream(files)
                .map(File::getName)
                // the file name may consist of combination of whitespace and non-whitespace characters and must end either with .xml or .json suffix
                .filter(fileName -> Codecs.SupportedDiseaseCaseFormat.getRegexMap().values().stream().anyMatch(fileName::matches))
                .collect(Collectors.toList());

        boolean useXML, useJSON;
        useXML = fileNameList.stream().allMatch(fileName -> fileName.matches(Codecs.SupportedDiseaseCaseFormat.getRegexMap().get(Codecs.SupportedDiseaseCaseFormat.XML)));
        useJSON = fileNameList.stream().allMatch(fileName -> fileName.matches(Codecs.SupportedDiseaseCaseFormat.getRegexMap().get(Codecs.SupportedDiseaseCaseFormat.JSON)));

        if (useXML) { // the directory contains only XML-encoded files
            return Codecs.SupportedDiseaseCaseFormat.XML;
        } else if (useJSON) { // the directory contains only JSON-encoded files
            return Codecs.SupportedDiseaseCaseFormat.JSON;
        } else { // the directory contains a mixture of XML and JSON-encoded files. Let user choose which to use
            return PopUps.getToggleChoiceFromUser(Arrays.asList(Codecs.SupportedDiseaseCaseFormat.values()), "Choose which format to use", "Model directory contains models stored in multiple formats")
                    .orElse(null);
        }
    }

    /**
     * Use this method to read all the {@link DiseaseCase} models present in the <code>where</code> directory
     *
     * @param where {@link File} representing existing but possibly empty directory with files containing {@link DiseaseCase}
     *              data.
     * @return possibly empty {@link Map} of files and corresponding {@link DiseaseCase}s
     */
    private static Map<File, DiseaseCase> readDiseaseCasesFromDirectory(File where) {
        if (where == null || !where.isDirectory()) {
            PopUps.showInfoMessage("Set curated files directory first", "Unable to show curated publications");
            return Collections.emptyMap();
        }

        // at this point `where` is a directory (possibly empty)
        // we will only consider disease cases stored in `formatToUse` (either JSON or XML at this point)

        LOGGER.debug("Figuring out how to parse data in '{}'", where);
        Codecs.SupportedDiseaseCaseFormat formatToUse = figureOutWhichFormatToUse(where);
        if (formatToUse == null) {
            LOGGER.warn("Unable to choose the proper way to decode the data, asking user");
            Optional<Codecs.SupportedDiseaseCaseFormat> toggleChoiceFromUser = PopUps.getToggleChoiceFromUser(Arrays.asList(Codecs.SupportedDiseaseCaseFormat.values()),
                    String.format("What encoding is used for models in \n'%s'?", where),
                    "Read data");
            if (toggleChoiceFromUser.isPresent()) {
                formatToUse = toggleChoiceFromUser.get();
            } else {
                return Collections.emptyMap();
            }
        }
        final Codecs.SupportedDiseaseCaseFormat selectedFormat = formatToUse;

        // create decoding function
        Function<InputStream, Optional<DiseaseCase>> decodingFunction;
        switch (selectedFormat) {
            case JSON:
                decodingFunction = ProtoJSONModelParser::readDiseaseCase;
                break;
            case XML:
                decodingFunction = XMLModelParser::loadDiseaseCase;
                break;
            default:
                LOGGER.warn("Unable to create decoder for selected format '{}'", selectedFormat);
                return Collections.emptyMap();
        }

        // decode the files
        File[] files = where.listFiles(f -> f.getName().matches(Codecs.SupportedDiseaseCaseFormat.getRegexMap().get(selectedFormat)));

        Map<File, DiseaseCase> models = new HashMap<>();
        for (File path : files) { // this should not be null (precondition in javadoc)
            try (InputStream is = new FileInputStream(path)) {
                decodingFunction.apply(is)
                        .ifPresent(dc -> models.put(path, dc));
            } catch (IOException e) {
                LOGGER.warn("Unable to decode file '{}'", path.getAbsolutePath(), e);
            }
        }
        return ImmutableMap.copyOf(models);
    }

    /**
     * Present a new case in this controller. The case is presented as a new Tab.
     * <p>
     * <b>!! IMPORTANT !!</b> - this method is the only place where a new case should added into the screen.
     *
     * @param diseaseCase      {@link DiseaseCase} to be presented
     * @param currentModelPath {@link Path} to file where the case was read from
     */
    private void addTab(DiseaseCase diseaseCase, Path currentModelPath) {
        try {
            final Tab tab = new Tab();
            final FXMLLoader loader = new FXMLLoader(DiseaseCaseDataController.class.getResource("DiseaseCaseDataView.fxml"));
            loader.setResources(resourceBundle);
            loader.setControllerFactory(injector::getInstance);
            tab.setContent(loader.load());

            final DiseaseCaseDataController controller = loader.getController();
            controller.presentData(diseaseCase);
            controller.setCurrentModelPath(currentModelPath);
            tab.textProperty().bind(controller.diseaseCaseTitleBinding());
            tab.setOnCloseRequest(e -> {
                int tabIdx = contentTabPane.getTabs().indexOf(tab);
                contentTabPane.getTabs().remove(tabIdx);
                controllers.remove(tabIdx);
            });
            contentTabPane.getTabs().add(tab);
            controllers.add(controller);
        } catch (IOException e) {
            LOGGER.warn("Error occured during initialization of the data view.", e);
        }
    }


    @FXML
    void newMenuItemAction() {
        addTab(DiseaseCase.getDefaultInstance(), null);
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

        List<File> files = filechooser.showOpenMultipleDialog(primaryStage);

        if (files == null) {
            // no file was selected
            return;
        }


        for (File file : files) {
            DiseaseCase diseaseCase;
            switch (filechooser.getSelectedExtensionFilter().getDescription()) {
                case SPLICING_XML:
                    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                        diseaseCase = XMLModelParser.loadDiseaseCase(is)
                                .orElseThrow(() -> new IOException("Unable to decode XML file"));
                    } catch (IOException e) {
                        PopUps.showException("Open XML model", "Sorry", "Unable to decode XML file", e);
                        return;
                    }
                    break;
                case PROTO_JSON:
                    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                        diseaseCase = ProtoJSONModelParser.readDiseaseCase(is)
                                .orElseThrow(() -> new IOException("Unable to decode JSON file"));
                    } catch (FileNotFoundException e) {
                        PopUps.showException("Open JSON model", String.format("File '%s' not found", file.getAbsolutePath()), "", e);
                        return;
                    } catch (InvalidProtocolBufferException e) {
                        PopUps.showException("Open JSON model", String.format("Error parsing file content '%s'", file.getAbsolutePath()), "", e);
                        return;
                    } catch (IOException e) {
                        PopUps.showException("Open JSON model", String.format("Error while reading file '%s'", file.getAbsolutePath()), "", e);
                        return;
                    }
                    break;
                default:
                    throw new RuntimeException("This should not have had happened!");
            }
            addTab(diseaseCase, file.toPath());
        }
    }

    @FXML
    void saveMenuItemAction() {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        DiseaseCaseDataController controller = controllers.get(selectedTabIdx);
        Path currentModelPath = controller.getCurrentModelPath();
        Path actualPath = saveModel(currentModelPath, controller.getData());

        // user might have selected to save the model elsewhere, here we reflect the update
        controller.setCurrentModelPath(actualPath);
    }

    @FXML
    void saveAsMenuItemAction() {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        DiseaseCaseDataController controller = controllers.get(selectedTabIdx);
        Path actualPath = saveModel(null, controller.getData());

        // user might have selected to save the model elsewhere, here we reflect the update
        controller.setCurrentModelPath(actualPath);
    }

    @FXML
    void exitMenuItemAction() {
        Platform.exit();
    }

    @FXML
    void showEditCurrentPublicationMenuItemAction() {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();

        try {
            Publication publication = controllers.get(selectedTabIdx).getData().getPublication();
            ShowEditPublicationController controller = new ShowEditPublicationController(publication);

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
            PopUps.showException("Edit Metadata of the current publication", "Error", "Unable to show dialog for editing of the current publication", e);
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
            PopUps.showException("Initialize Hpo Case Annotator resources", "Error", "Unable to display dialog for setting resources", e);
        }
    }

    @FXML
    void showCuratedPublicationsMenuItemAction() {
        File where = optionalResources.getDiseaseCaseDir();
        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(where);

        try {
            ShowPublicationsController controller = new ShowPublicationsController(hostServices);
            controller.setData(models.values());
            Parent parent = FXMLLoader.load(ShowPublicationsController.class.getResource("ShowPublicationsView.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            Stage stage = new Stage();
            stage.setTitle("Curated publications in '" + where.getAbsolutePath() + "'");
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for showing curated publications", e);
            PopUps.showException("Show curated publications", "Error", "Unable to display dialog for showing curated publications", e);
        }
    }

    @FXML
    void validateCurrentEntryMenuItemAction() {

        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();

        DiseaseCase theCase = controllers.get(selectedTabIdx).getData();
        ValidationRunner<DiseaseCase> runner = ValidationRunner.forAllValidations(assemblies);
        List<ValidationResult> results = runner.validateSingleModel(theCase);

        try {
            ShowValidationResultsController controller = new ShowValidationResultsController();
            Parent parent = FXMLLoader.load(ShowValidationResultsController.class.getResource("ShowValidationResults.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setValidationResult(theCase, results);
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
        File where = optionalResources.getDiseaseCaseDir();
        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(where);
        if (models.isEmpty()) {
            PopUps.showInfoMessage(String.format("No models in directory %s", where.getPath()), "Validate all cases");
            return;
        }

        ValidationRunner<DiseaseCase> runner = ValidationRunner.forAllValidations(assemblies);
        Map<DiseaseCase, List<ValidationResult>> validationResults = runner.validateModels(models.values());

        try {
            ShowValidationResultsController controller = new ShowValidationResultsController();
            Parent parent = FXMLLoader.load(ShowValidationResultsController.class.getResource("ShowValidationResults.fxml"),
                    resourceBundle, new JavaFXBuilderFactory(), clazz -> controller);
            controller.setValidationResults(validationResults);
            Stage stage = new Stage();
            stage.setTitle("Validate all cases");
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.warn("Unable to display dialog for validation of the curated publications", e);
        }
    }


    @FXML
    void exportToCSVMenuItemAction() {
        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(optionalResources.getDiseaseCaseDir().getAbsoluteFile());

        ModelExporter exporter = new TSVModelExporter("\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants to CSV file", "variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                exporter.exportModels(models.values(), writer);
                LOGGER.info("Exported {} cases", models.size());
            } catch (IOException e) {
                LOGGER.warn(String.format("Error occured during variant export: %s", e.getMessage()), e);
            }
        }
    }


    @FXML
    private void exportPhenopacketAllCasesMenuItemAction() {

        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(optionalResources.getDiseaseCaseDir());

        File where = PopUps.selectDirectory(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Select export directory");

        DiseaseCaseToPhenopacketCodec codec = Codecs.diseaseCasePhenopacketCodec();

        if (where != null) {
            int counter = 0;
            for (DiseaseCase model : models.values()) {
                // we're exporting in JSON format
                String fileName = ModelUtils.getFileNameWithSampleId(model) + ".json";
                File target = new File(where, fileName);
                try (BufferedWriter writer = Files.newBufferedWriter(target.toPath())) {
                    final Phenopacket packet = codec.encode(model);
                    LOGGER.trace("Writing phenopacket to '{}'", target);
                    String jsonString = PRINTER.print(packet);
                    writer.write(jsonString);
                    counter++;
                } catch (IOException e) {
                    LOGGER.warn("Error occurred during phenopacket export", e);
                    PopUps.showException("Error", "Error occurred during phenopacket export", e.getMessage(), e);
                }
            }
            LOGGER.info("Exported {} phenopackets", counter);
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
    public void exportPhenopacketCurrentCaseMenuItemAction() {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();


        DiseaseCase diseaseCase = controllers.get(selectedTabIdx).getData();
        String suggestedFileName = ModelUtils.getFileNameWithSampleId(diseaseCase) + ".json";
        String title = "Save as Phenopacket";

        final FileChooser filechooser = new FileChooser();
        filechooser.setTitle(title);
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setInitialFileName(suggestedFileName);
        FileChooser.ExtensionFilter jsonFormat = new FileChooser.ExtensionFilter("Phenopacket in JSON format", "*.phenopacket");
        filechooser.getExtensionFilters().add(jsonFormat);
        filechooser.setSelectedExtensionFilter(jsonFormat);

        File where = filechooser.showSaveDialog(primaryStage);
        DiseaseCaseToPhenopacketCodec codec = Codecs.diseaseCasePhenopacketCodec();
        if (where != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(where.toPath())) {
                final Phenopacket packet = codec.encode(diseaseCase);
                String jsonString = PRINTER.print(packet);
                writer.write(jsonString);
            } catch (IOException e) {
                LOGGER.warn("Error occured during Phenopacket export", e);
                PopUps.showException(title, "Error occured during Phenopacket export", e.getMessage(), e);
            }
        }

    }

    /**
     * Encode all cases stored in <em>curated files directory</em> into Phenopacket format and store them in specified
     * directory.
     * <p>
     * Data specific to splicing is encoded into {@link Phenopacket}.
     */
    @FXML
    public void exportPhenopacketAllCasesForBassMenuItemAction() {
        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(optionalResources.getDiseaseCaseDir());

        File where = PopUps.selectDirectory(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Select export directory");
        DiseaseCaseToBassPhenopacketCodec codec = Codecs.bassPhenopacketCodec();

        if (where != null) {
            int counter = 0;
            for (DiseaseCase model : models.values()) {
                // we're exporting in JSON format
                String fileName = ModelUtils.getFileNameWithSampleId(model) + ".json";
                File target = new File(where, fileName);
                try (BufferedWriter writer = Files.newBufferedWriter(target.toPath())) {
                    final Phenopacket packet = codec.encode(model);
                    LOGGER.trace("Writing phenopacket to '{}'", target);
                    String jsonString = PRINTER.print(packet);
                    writer.write(jsonString);
                    counter++;
                } catch (IOException e) {
                    LOGGER.warn("Error occurred during phenopacket export", e);
                    PopUps.showException("Error", "Error occurred during phenopacket export", e.getMessage(), e);
                }
            }
            LOGGER.info("Exported {} phenopackets", counter);
            PopUps.showInfoMessage(String.format("Exported %d phenopackets", counter), "Export phenopackets");
        }
    }

    @FXML
    void exportPhenoModelsMenuItemAction() {
        Map<File, DiseaseCase> models = readDiseaseCasesFromDirectory(optionalResources.getDiseaseCaseDir());

        // write the cases to selected file
        ModelExporter phenoExporter = new PhenoModelExporter("\t");
        File where = PopUps.selectFileToSave(primaryStage, optionalResources.getDiseaseCaseDir(),
                "Export variants in format required by GPI project", "gpi_variants.tsv");
        if (where != null) {
            try (Writer writer = new FileWriter(where)) {
                phenoExporter.exportModels(models.values(), writer);
                LOGGER.info("Exported {} models", models.size());
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

        contentTabPane.getTabs().addListener(menuItemDisablerFor(saveMenuItem));
        contentTabPane.getTabs().addListener(menuItemDisablerFor(saveAsMenuItem));
        contentTabPane.getTabs().addListener(menuItemDisablerFor(saveAllMenuItem));
        contentTabPane.getTabs().addListener(menuItemDisablerFor(validateCurrentEntryMenuItem));
        contentTabPane.getTabs().addListener(menuItemDisablerFor(exportPhenopacketMenuItem));

        // disable for now - TODO - enable saving only if the disease case is complete?
//        saveMenuItem.disableProperty().bind(dataController.diseaseCaseIsComplete().not());
        showCuratedPublicationsMenuItem.disableProperty().bind(optionalResources.diseaseCaseDirIsInitializedProperty().not());

    }

    /**
     * @return {@link ListChangeListener} for tab list that disables the {@link #saveAllMenuItem} if the tab list is empty.
     */
    private ListChangeListener<Tab> menuItemDisablerFor(final MenuItem menuItem) {
        return c -> {
            if (c.getList().isEmpty()) {
                menuItem.setDisable(true);
            } else {
                menuItem.setDisable(false);
            }
        };
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
    private Path saveModel(Path currentModelPath, DiseaseCase model) {
//        CompletenessValidator completenessValidator = new CompletenessValidator();
//        completenessValidator.validateDiseaseCase(model);
        String conversationTitle = "Save data into file";


        if (currentModelPath == null) {
            FileChooser fileChooser = new FileChooser();

            final String SPLICING_XML = "XML data format (*.xml)";
            final String PROTO_JSON = "JSON data format (*.json)";
            FileChooser.ExtensionFilter xmlFileFormat = new FileChooser.ExtensionFilter(SPLICING_XML, "*.xml");
            FileChooser.ExtensionFilter jsonFileFormat = new FileChooser.ExtensionFilter(PROTO_JSON, "*.json");
            //        fileChooser.setSelectedExtensionFilter(jsonFileFormat);

            fileChooser.setTitle(conversationTitle);
            String suggestedName = ModelUtils.getFileNameWithSampleId(model) + ".json";
            fileChooser.setInitialFileName(suggestedName);
            fileChooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
            fileChooser.getExtensionFilters().addAll(jsonFileFormat, xmlFileFormat);
            File which = fileChooser.showSaveDialog(primaryStage);

            if (which == null) {
                return null;
            }

            while (which.getName().matches(".*\\s.*")) { // at least one whitespace with or without surrounding characters
                if (PopUps.getBooleanFromUser("Provide new name or cancel", String.format("File name '%s' contains whitespace character", which.getName()), "Warning")) {
                    which = fileChooser.showSaveDialog(primaryStage);
                } else {
                    return null;
                }
            }
            currentModelPath = which.toPath();

            if (fileChooser.getSelectedExtensionFilter().getDescription().equals(jsonFileFormat.getDescription())) {
                try (OutputStream os = Files.newOutputStream(currentModelPath)) {
                    ProtoJSONModelParser.saveDiseaseCase(os, model, Charset.forName("UTF-8")); // TODO - charset is hardcoded
                } catch (IOException e) {
                    PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                    LOGGER.warn("Unable to store data into file {}", currentModelPath, e);
                    return null;
                }
            } else if (fileChooser.getSelectedExtensionFilter().getDescription().equals(xmlFileFormat.getDescription())) {
                try (OutputStream os = Files.newOutputStream(currentModelPath)) {
                    XMLModelParser.saveDiseaseCase(model, os);
                } catch (IOException e) {
                    PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                    LOGGER.warn("Unable to store data into file {}", currentModelPath, e);
                    return null;
                }
            }
        } else {
            // update the Biocurator ID
            model = model.toBuilder()
                    .setBiocurator(Biocurator.newBuilder().setBiocuratorId(optionalResources.getBiocuratorId()).build())
                    .build();
            try (OutputStream os = Files.newOutputStream(currentModelPath)) {
                // save in the same format the model was saved
                if (currentModelPath.toFile().getName().endsWith(".xml")) {
                    XMLModelParser.saveDiseaseCase(model, os);
                } else if (currentModelPath.toFile().getName().endsWith(".json")) {
                    ProtoJSONModelParser.saveDiseaseCase(os, model, Charset.forName("UTF-8")); // TODO - charset is hardcoded
                }
            } catch (IOException e) {
                PopUps.showException(conversationTitle, "Unable to store data into file", "", e);
                LOGGER.warn("Unable to store data into file {}", currentModelPath, e);
                return null;
            }
        }

        // SUCCESS (if we got here)
        PopUps.showInfoMessage(String.format("Data saved into file %s", currentModelPath.toFile().getName()), conversationTitle);
        return currentModelPath;
    }


    @FXML
    public void saveAllMenuItemAction() {
        controllers.forEach(c -> saveModel(c.getCurrentModelPath(), c.getData()));
    }
}
