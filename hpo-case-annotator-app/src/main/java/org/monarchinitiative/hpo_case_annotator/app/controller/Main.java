package org.monarchinitiative.hpo_case_annotator.app.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.hpo_case_annotator.app.dialogs.Dialogs;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.StudyType;
import org.monarchinitiative.hpo_case_annotator.app.publication.PublicationBrowser;
import org.monarchinitiative.hpo_case_annotator.convert.ConversionCodecs;
import org.monarchinitiative.hpo_case_annotator.forms.liftover.LiftoverController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.disease.DiseaseStatusController;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.app.io.PubmedIO;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.CohortStudyController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.FamilyStudyController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.StudyController;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeBrowserController;
import org.monarchinitiative.hpo_case_annotator.io.ModelParsers;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Component
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String SEE_LOG_FOR_MORE_DETAILS = "See log for more details.";

    private final OptionalResources optionalResources;
    private final HCAControllerFactory controllerFactory;
    private final ExecutorService executorService;
    private final PublicationBrowser publicationBrowser;

    /**
     * This list contains data wrappers in the same order as they are present in the {@link #studiesTabPane}.
     * <p>
     * The only place where items are added into this list is the {@link #addStudy(URL, StudyWrapper)} method.
     */
    private final List<StudyWrapper<?>> wrappers = new LinkedList<>();

    /* **************************************************   FILE   ************************************************** */
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem closeMenuItem;

    /* *************************************************    VIEW      *********************************************** */
    @FXML
    private MenuItem cloneCaseMenuItem;

    /* *************************************************    STUDY     *********************************************** */
    @FXML
    private Menu studyMenu;
    @FXML
    private MenuItem fetchFromPubmedMenuItem;
    @FXML
    private MenuItem viewOnPubmedMenuItem;

    /* *************************************************   PROJECT    *********************************************** */
    @FXML
    private MenuItem showCuratedPublicationsMenuItem;
    @FXML
    private MenuItem showCuratedVariantsMenuItem;

    /* ************************************************   GENOTYPE    *********************************************** */
    @FXML
    private Menu genotypeMenu;

    /* ************************************************   PHENOTYPE  ************************************************ */
    @FXML
    private Menu phenotypeMenu;
    @FXML
    private MenuItem editPhenotypeFeaturesMenuItem;

    /* ************************************************    DISEASE  ************************************************* */
    @FXML
    private Menu diseaseMenu;
    @FXML
    private MenuItem editDiseaseMenuItem;

    /* ************************************************   VALIDATE  ************************************************* */

    @FXML
    private MenuItem validateCurrentEntryMenuItem;
    @FXML
    private MenuItem exportPhenopacketMenuItem;

    /* ************************************************     HELP    ************************************************* */


    /* ************************************************   THE REST   ************************************************* */


    @FXML
    private TabPane studiesTabPane;

    @FXML
    private HBox statusBar;

    @FXML
    private StatusBarController statusBarController;

    public Main(OptionalResources optionalResources,
                HCAControllerFactory controllerFactory,
                ExecutorService executorService,
                PublicationBrowser publicationBrowser) {
        this.optionalResources = optionalResources;
        this.controllerFactory = controllerFactory;
        this.executorService = executorService;
        this.publicationBrowser = publicationBrowser;
    }

    @FXML
    private void initialize() {
        disableMenuEntriesDependentOnADataModel();
    }

    private void disableMenuEntriesDependentOnADataModel() {
        BooleanBinding noTabIsPresent = studiesTabPane.getSelectionModel().selectedItemProperty().isNull();
        saveMenuItem.disableProperty().bind(noTabIsPresent);
        saveAsMenuItem.disableProperty().bind(noTabIsPresent);
        saveAllMenuItem.disableProperty().bind(noTabIsPresent);
        closeMenuItem.disableProperty().bind(noTabIsPresent);

        cloneCaseMenuItem.disableProperty().bind(noTabIsPresent);
        studyMenu.disableProperty().bind(noTabIsPresent);
        fetchFromPubmedMenuItem.disableProperty().bind(noTabIsPresent);
        viewOnPubmedMenuItem.disableProperty().bind(noTabIsPresent);

        genotypeMenu.disableProperty().bind(noTabIsPresent);
        phenotypeMenu.disableProperty().bind(noTabIsPresent);
        diseaseMenu.disableProperty().bind(noTabIsPresent);

        validateCurrentEntryMenuItem.disableProperty().bind(noTabIsPresent);
    }

    /*                                                 FILE                                                           */

    @FXML
    private void newMenuItemAction(ActionEvent e) {
        var individualStudy = new CommandLinksDialog.CommandLinksButtonType("Individual study", "Enter data about a single individual (a cohort with size 1).", true);
        var familyStudy = new CommandLinksDialog.CommandLinksButtonType("Family study", "Enter data about several related individuals.", false);
        var cohortStudy = new CommandLinksDialog.CommandLinksButtonType("Cohort study", "Enter data about several unrelated individuals.", false);
        CommandLinksDialog dialog = new CommandLinksDialog(individualStudy, familyStudy, cohortStudy);
        dialog.setTitle("New study");
        dialog.setHeaderText("Select study type.");
        dialog.setContentText("HCA supports the following study types:");
        Optional<ButtonType> buttonType = dialog.showAndWait();

        Optional<StudyType> studyType = buttonType.flatMap(bt -> {
            if (bt.equals(individualStudy.getButtonType()) || bt.equals(cohortStudy.getButtonType())) {
                return Optional.of(StudyType.COHORT);
            } else if (bt.equals(familyStudy.getButtonType())) {
                return Optional.of(StudyType.FAMILY);
            } else {
                return Optional.empty();
            }
        });
        studyType.flatMap(Main::controllerUrlForStudyType)
                .ifPresent(url -> addStudy(url, StudyWrapper.of(studyForStudyType(studyType.get()))));
        e.consume();
    }

    private static Optional<URL> controllerUrlForStudyType(StudyType type) {
        return switch (type) {
            case FAMILY -> Optional.ofNullable(FamilyStudyController.class.getResource("FamilyStudy.fxml"));
            case COHORT -> Optional.ofNullable(CohortStudyController.class.getResource("CohortStudy.fxml"));
        };
    }

    private static ObservableStudy studyForStudyType(StudyType studyType) {
        return switch (studyType) {
            case FAMILY -> new ObservableFamilyStudy();
            case COHORT -> new ObservableCohortStudy();
        };
    }

    private <T extends ObservableStudy> void addStudy(URL fxmlUrl, StudyWrapper<T> wrapper) {
        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(controllerFactory);

            Tab tab = new Tab();
            tab.setContent(loader.load());

            StudyController<T> controller = loader.getController();
            controller.setData(wrapper.study());

            tab.textProperty().bind(controller.getData().idProperty());
            tab.setOnCloseRequest(e -> removeStudy(studiesTabPane.getTabs().indexOf(tab)));

            studiesTabPane.getTabs().add(tab);
            studiesTabPane.getSelectionModel().select(tab);
            wrappers.add(wrapper);
        } catch (IOException e) {
            LOGGER.warn("Error loading study: {}", e.getMessage(), e);
        }
    }

    private void removeStudy(int tabIdx) {
        studiesTabPane.getTabs().remove(tabIdx);
        wrappers.remove(tabIdx);
    }

    @FXML
    private void openMenuItemAction(ActionEvent e) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setTitle("Open study");

        String HCA_JSON = "HCA JSON data format (*.json)";
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter(HCA_JSON, "*.json");
        filechooser.getExtensionFilters().addAll(jsonFilter);
        filechooser.setSelectedExtensionFilter(jsonFilter);

        List<File> files = filechooser.showOpenMultipleDialog(getOwnerWindow());

        if (files == null)
            return;

        for (File file : files) {
            readStudy(file).flatMap(Convert::toObservableStudy)
                    .ifPresent(observableStudy -> studyTypeForStudy(observableStudy)
                            .flatMap(Main::controllerUrlForStudyType)
                            .ifPresent(url -> addStudy(url, StudyWrapper.of(observableStudy, file.toPath())))
                    );
        }
        e.consume();
    }

    private static Optional<Study> readStudy(File studyFile) {
        Study study = null;

        // try v1 first
        try {
            DiseaseCase dc = ModelParsers.V1.jsonParser().read(studyFile.toPath());
            study = ConversionCodecs.v1ToV2().encode(dc);
        } catch (InvalidProtocolBufferException pbe) {
            // The following holds if we're trying to read v2 model.
            if (!pbe.getMessage().contains("Cannot find field: id in message org.monarchinitiative.hpo_case_annotator_model.proto.DiseaseCase")) {
                Dialogs.showWarningDialog("Error", String.format("Unable to read study at '%s'", studyFile.getAbsolutePath()), SEE_LOG_FOR_MORE_DETAILS);
                LOGGER.warn("Unable to read study at '{}'", studyFile.getAbsolutePath(), pbe);
            }
        } catch (IOException | ModelTransformationException ex) {
            Dialogs.showWarningDialog("Error", String.format("Unable to read study at '%s'", studyFile.getAbsolutePath()), SEE_LOG_FOR_MORE_DETAILS);
            LOGGER.warn("Unable to read study at '{}'", studyFile.getAbsolutePath(), ex);
        }

        // then try v2
        if (study == null) {
            try {
                study = ModelParsers.V2.jsonParser().read(studyFile.toPath());
            } catch (IOException ex) {
                Dialogs.showWarningDialog("Error", "Error reading v2 case", SEE_LOG_FOR_MORE_DETAILS);
                LOGGER.warn("Error reading v2 study at '{}'", studyFile.getAbsolutePath(), ex);
                return Optional.empty();
            }
        }

        if (study == null) {
            Dialogs.showWarningDialog("Sorry", "Unable to read study data", "Unknown format");
        }

        return Optional.ofNullable(study);
    }

    private static Optional<StudyType> studyTypeForStudy(ObservableStudy study) {
        if (study instanceof ObservableFamilyStudy) {
            return Optional.of(StudyType.FAMILY);
        } else if (study instanceof ObservableCohortStudy) {
            return Optional.of(StudyType.COHORT);
        } else {
            return Optional.empty();
        }
    }

    private Window getOwnerWindow() {
        return studiesTabPane.getScene().getWindow();
    }

    @FXML
    private void saveMenuItemAction(ActionEvent e) {
        int tabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.study(), wrapper.studyPath());
        e.consume();
    }

    private boolean saveStudy(Object data, Path path) {
        return convertToStudy(data)
                .map(study -> saveV2Study(study, path))
                .orElse(false);
    }

    private static Optional<Study> convertToStudy(Object data) {
        if (data instanceof DiseaseCase dc) {
            try {
                return Optional.of(ConversionCodecs.v1ToV2().encode(dc));
            } catch (ModelTransformationException e) {
                LOGGER.warn("Error serializing study: {}", e.getMessage(), e);
                return Optional.empty();
            }
        } else if (data instanceof ObservableStudy study) {
            return Convert.toStudy(study);
        } else {
            LOGGER.warn("Unknown study class: `{}`", data.getClass());
            return Optional.empty();
        }
    }

    private boolean saveV2Study(Study study, Path path) {
        if (path == null) {
            path = askForPath(study.id());
        }

        if (path == null) // the user canceled
            return false;

        try {
            ModelParsers.V2.jsonParser().write(study, path);
            return true;
        } catch (IOException e) {
            LOGGER.warn("Error serializing study: {}", e.getMessage(), e);
            return false;
        }
    }

    private Path askForPath(String studyId) {
        FileChooser fileChooser = new FileChooser();

        final String PROTO_JSON = "JSON data format (*.json)";

        FileChooser.ExtensionFilter jsonFileFormat = new FileChooser.ExtensionFilter(PROTO_JSON, "*.json");
        fileChooser.setSelectedExtensionFilter(jsonFileFormat);

        fileChooser.setTitle("Save study");
        String suggestedName = studyId + ".json";
        fileChooser.setInitialFileName(suggestedName);
        fileChooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        fileChooser.getExtensionFilters().add(jsonFileFormat);
        File which = fileChooser.showSaveDialog(getOwnerWindow());

        return (which == null)
                ? null
                : which.toPath();
    }

    @FXML
    private void saveAsMenuItemAction(ActionEvent e) {
        int tabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.study(), null);
        e.consume();
    }

    @FXML
    private void saveAllMenuItemAction(ActionEvent e) {
        for (StudyWrapper<?> wrapper : wrappers) {
            if (!saveStudy(wrapper.study(), wrapper.studyPath())) {
                // user cancelled
                break;
            }
        }
        e.consume();
    }

    @FXML
    private void closeMenuItemAction(ActionEvent e) {
        Dialogs.getBooleanFromUser("Close study", "Do you want to close the current study?", null)
                .filter(bt -> bt.equals(ButtonType.OK))
                .ifPresent(bt -> removeStudy(studiesTabPane.getSelectionModel().getSelectedIndex()));
        e.consume();
    }

    @FXML
    private void exportToCSVMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportToSummaryFileMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportPhenopacketCurrentCaseMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportPhenopacketAllCasesMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void setResourcesMenuItemAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(SetResourcesController.class.getResource("SetResources.fxml"));
            loader.setControllerFactory(controllerFactory);

            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(getOwnerWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Initialize resources");
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (IOException ex) {
            LOGGER.warn("Error setting up resources: {}", ex.getMessage(), ex);
        }
        e.consume();
    }

    @FXML
    private void exitMenuItemAction(ActionEvent e) {
        e.consume();
        Dialogs.getBooleanFromUser("Exit", "Are you sure you want to quit?", null)
                .filter(buttonType -> buttonType.equals(ButtonType.OK))
                .ifPresent(buttonType -> Platform.exit());
    }

    @FXML
    private void cloneCaseMenuItemAction(ActionEvent e) {
        Optional<Object> studyData = getCurrentStudyData();
        Optional<StudyType> studyType = studyData.flatMap(Main::studyTypeForData);

        studyData.ifPresent(data -> {
            if (data instanceof ObservableStudy study) {
                studyType.flatMap(Main::controllerUrlForStudyType)
                        .ifPresent(url -> addStudy(url, StudyWrapper.of(study)));
            }
        });

        // TODO - make it work for all study/disease case types
        e.consume();

    }

    private static Optional<StudyType> studyTypeForData(Object data) {
        if (data instanceof ObservableFamilyStudy || data instanceof FamilyStudy) {
            return Optional.of(StudyType.FAMILY);
        } else if (data instanceof ObservableCohortStudy || data instanceof CohortStudy) {
            return Optional.of(StudyType.COHORT);
        } else {
            LOGGER.warn("Unknown study data class: {}", data.getClass());
            return Optional.empty();
        }
    }

    private Optional<Object> getCurrentStudyData() {
        return getCurrentStudyWrapper()
                .map(StudyWrapper::study);
    }

    private Optional<StudyWrapper<?>> getCurrentStudyWrapper() {
        if (!studiesTabPane.getSelectionModel().isEmpty()) {
            int selectedStudyTabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
            return Optional.of(wrappers.get(selectedStudyTabIdx));
        }
        return Optional.empty();
    }

    /*                                                 VIEW                                                           */
    @FXML
    private void fetchFromPubmedMenuItemAction(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Fetch publication from PubMed");
        dialog.setHeaderText("Enter PMID number");
        dialog.setContentText("E.g. 34289339");
        dialog.initOwner(getOwnerWindow());
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter());
        dialog.getEditor().setTextFormatter(formatter);

        dialog.showAndWait().ifPresent(pmid -> {
            Task<Publication> task = PubmedIO.v2publication(pmid);

            // trip to PubMed was successful
            task.setOnSucceeded(we -> Platform.runLater(() -> {
                Publication publication = task.getValue();

                getCurrentStudyData().ifPresent(data -> {
                    if (data instanceof ObservableStudy study) {
                        Convert.toObservablePublication(publication, study.getPublication());
                    } else {
                        // TODO - support?
                        Dialogs.showWarningDialog("Sorry", "PubMed import is not supported for v1 model", null);
                    }
                });
            }));

            // No avail
            task.setOnFailed(we -> {
                Throwable throwable = we.getSource().getException();
                Dialogs.showWarningDialog("Error", "Error fetching PubMed data for " + pmid, SEE_LOG_FOR_MORE_DETAILS);
                LOGGER.warn("Error fetching PubMed data for " + pmid, throwable);
            });
            executorService.submit(task);
        });

        e.consume();
    }

    @FXML
    private void viewOnPubmedMenuItemAction(ActionEvent e) {
        e.consume();
        getCurrentStudyData().ifPresent(data -> {
            String pmid;
            if (data instanceof ObservableStudy study) {
                pmid = study.getPublication().getPmid();
            } else if (data instanceof DiseaseCase dc) {
                pmid = dc.getPublication().getPmid();
            } else {
                LOGGER.warn("Unable to to get PMID from {}", data.getClass().getName());
                return;
            }
            publicationBrowser.browse(pmid);
        });

    }

    /*                                                 PROJECT                                                        */
    @FXML
    private void showCuratedPublicationsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void showCuratedVariantsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    /*                                                 GENOTYPE                                                       */



    /*                                                 PHENOTYPE                                                      */
    @FXML
    private void editPhenotypicFeaturesMenuItemAction(ActionEvent e) {
        getCurrentStudyData()
                .flatMap(getBaseObservableIndividualBinding())
                .ifPresent(this::editPhenotypeFeatures);

        e.consume();
    }

    private <T extends BaseObservableIndividual> void editPhenotypeFeatures(ObjectBinding<T> individual) {
        try {
            FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeBrowser.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // setup & bind controller
            PhenotypeBrowserController<T> controller = loader.getController();
            controller.ontologyProperty().bind(optionalResources.ontologyProperty());
            controller.dataProperty().bind(individual);

            // show the phenotype stage
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(getOwnerWindow());
            stage.setTitle(String.format("Phenotype features of %s", individual.get().getId()));
            stage.setScene(new Scene(parent));
            stage.showAndWait();

            // unbind
            controller.ontologyProperty().unbind();
            controller.dataProperty().unbind();
        } catch (IOException e) {
            Dialogs.showErrorDialog("Error", "Error occurred while adding phenotype features", SEE_LOG_FOR_MORE_DETAILS);
            LOGGER.warn("Error adding phenotype features: {}", e.getMessage(), e);
        }
    }

    private Function<Object, Optional<? extends ObjectBinding<? extends BaseObservableIndividual>>> getBaseObservableIndividualBinding() {
        return data -> {
            Tab selectedStudyTab = studiesTabPane.getSelectionModel().getSelectedItem();
            if (data instanceof ObservableFamilyStudy || data instanceof ObservableCohortStudy) {
                TabPane membersTabPane = (TabPane) selectedStudyTab.getContent().getParent().lookup("#members-tab-pane");
                Integer tabIdx = membersTabPane.getSelectionModel().selectedIndexProperty().getValue();

                if (tabIdx == null || tabIdx == 0) {
                    // No tab is open, or summary tab is open.
                    Dialogs.showWarningDialog("Sorry", "Unable to open the dialog when no individual tab is open", "Open a tab in the pedigree/cohort");
                    return Optional.empty();
                }


                if (data instanceof ObservableFamilyStudy study) {
                    return Optional.of(Bindings.valueAt(study.getPedigree().members(), tabIdx - 1));
                } else if (data instanceof ObservableCohortStudy study) {
                    // I really want to have this branch for clarity
                    return Optional.of(Bindings.valueAt(study.members(), tabIdx - 1));
                } else {
                    return Optional.empty();
                }
            } else {
                Dialogs.showInfoDialog("Sorry", String.format("Working with '%s' is not yet implemented", data.getClass().getName()), null);
                return Optional.empty();
            }
        };
    }

    @FXML
    private void editDiseaseMenuItemAction(ActionEvent e) {
        getCurrentStudyData()
                .flatMap(getBaseObservableIndividualBinding())
                .ifPresent(this::editDisease);

        e.consume();
    }

    private <T extends BaseObservableIndividual> void editDisease(ObjectBinding<T> individual) {
        try {
            FXMLLoader loader = new FXMLLoader(DiseaseStatusController.class.getResource("DiseaseStatus.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // setup & bind controller
            DiseaseStatusController<T> controller = loader.getController();
            controller.dataProperty().bind(individual);

            // show the phenotype stage
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(getOwnerWindow());
            stage.setTitle(String.format("Disease data of %s", individual.get().getId()));
            stage.setScene(new Scene(parent));
            stage.showAndWait();

            // unbind
            controller.dataProperty().unbind();
        } catch (IOException e) {
            Dialogs.showErrorDialog("Error", "Error occurred while adding phenotype features", SEE_LOG_FOR_MORE_DETAILS);
            LOGGER.warn("Error adding phenotype features: {}", e.getMessage(), e);
        }
    }


    /*                                                 VALIDATE                                                       */
    @FXML
    private void validateCurrentEntryMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void validateAllModelsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    /*                                                 HELP                                                           */

    @FXML
    private void helpMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoDialog("Sorry", null, "Not yet implemented");
        e.consume();
    }

    @FXML
    private void liftoverAction(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(LiftoverController.class.getResource("Liftover.fxml"));
            loader.setControllerFactory(controllerFactory);

            Parent parent = loader.load();
            LiftoverController controller = loader.getController();
            controller.liftoverServiceProperty().bind(optionalResources.liftoverServiceProperty());

            Stage stage = new Stage();
            stage.initOwner(getOwnerWindow());
            stage.initModality(Modality.NONE);
            stage.setTitle("Liftover contig position");
            stage.setScene(new Scene(parent));
            stage.show();
            controller.liftoverServiceProperty().unbind();
        } catch (IOException ex) {
            Dialogs.showErrorDialog("Error", "Error occurred while opening the LiftOver window.", SEE_LOG_FOR_MORE_DETAILS);
            LOGGER.warn("Unable to display liftover dialog: {}", ex.getCause().getMessage());
        }

        e.consume();
    }

    /*                                                 OTHERS                                                         */

}
