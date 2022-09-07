package org.monarchinitiative.hpo_case_annotator.app.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.monarchinitiative.hpo_case_annotator.forms.nvo.BaseStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.nvo.CohortStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.nvo.FamilyStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.nvo.IndividualStudyComponent;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.app.io.PubmedIO;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.observable.v2.*;
import org.monarchinitiative.hpo_case_annotator.io.ModelParsers;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static javafx.beans.binding.Bindings.select;
import static javafx.beans.binding.Bindings.selectString;

@Controller
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String SEE_LOG_FOR_MORE_DETAILS = "See log for more details.";

    private final OptionalResources optionalResources;
    private final ExecutorService executorService;
    private final PublicationBrowser publicationBrowser;

    /**
     * This list contains data wrappers in the same order as they are present in the {@link #studiesTabPane}.
     * <p>
     * The items are added into the list exclusively in {@link #addStudy(StudyWrapper)}
     * and removed in {@link #removeStudy(int)}.
     */
    private final ObservableList<StudyWrapper<?>> wrappers = FXCollections.observableArrayList();

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
                ExecutorService executorService,
                PublicationBrowser publicationBrowser) {
        this.optionalResources = optionalResources;
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

        validateCurrentEntryMenuItem.disableProperty().bind(noTabIsPresent);
    }

    /*                                                 FILE                                                           */

    @FXML
    private void newMenuItemAction(ActionEvent e) {
        var individualStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.INDIVIDUAL.getName(), StudyType.INDIVIDUAL.getDescription(), true);
        var familyStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.FAMILY.getName(), StudyType.FAMILY.getDescription(), false);
        var cohortStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.COHORT.getName(), StudyType.COHORT.getDescription(), false);

        CommandLinksDialog dialog = new CommandLinksDialog(individualStudy, familyStudy, cohortStudy);

        dialog.setTitle("New study");
        dialog.setHeaderText("Select study type");
        dialog.setContentText("HCA supports the following study types:");
        Optional<ButtonType> buttonType = dialog.showAndWait();

        buttonType.flatMap(bt -> {
            if (bt.equals(individualStudy.getButtonType())) {
                return Optional.of(new ObservableIndividualStudy());
            } else if (bt.equals(cohortStudy.getButtonType())) {
                return Optional.of(new ObservableFamilyStudy());
            } else if (bt.equals(familyStudy.getButtonType())) {
                return Optional.of(new ObservableCohortStudy());
            } else {
                return Optional.empty();
            }
        })
                .flatMap(st -> wrapStudy(st, null))
                .ifPresent(this::addStudy);
        e.consume();
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
            readStudy(file.toPath())
                    .flatMap(st -> wrapStudy(st, file.toPath()))
                    .ifPresent(this::addStudy);
        }
        e.consume();
    }

    @FXML
    private void saveMenuItemAction(ActionEvent e) {
        int tabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.component(), wrapper.studyPath());
        e.consume();
    }

    @FXML
    private void saveAsMenuItemAction(ActionEvent e) {
        int tabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.component(), null);
        e.consume();
    }

    @FXML
    private void saveAllMenuItemAction(ActionEvent e) {
        for (StudyWrapper<?> wrapper : wrappers) {
            if (!saveStudy(wrapper.component(), wrapper.studyPath())) {
                // The user cancelled.
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
            // TODO - implement
//            loader.setControllerFactory(controllerFactory);

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
        getCurrentStudyWrapper()
                .flatMap(cloneStudyWrapper())
                .ifPresent(this::addStudy);
        e.consume();
    }

    /*                                                 VIEW                                                           */
    @FXML
    private void fetchFromPubmedMenuItemAction(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Fetch publication from PubMed");
        dialog.setHeaderText("Enter PMID number");
        dialog.setContentText("E.g. 18023225");
        dialog.initOwner(getOwnerWindow());
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter());
        dialog.getEditor().setTextFormatter(formatter);

        dialog.showAndWait().ifPresent(pmid -> {
            Task<Publication> task = PubmedIO.v2publication(pmid);

            // trip to PubMed was successful
            task.setOnSucceeded(we -> Platform.runLater(() -> {
                Publication publication = task.getValue();

                getCurrentStudyComponent()
                        .ifPresent(study -> {
                            ObservableStudy data = study.getData();
                            if (data != null)
                                data.setPublication(new ObservablePublication(publication));
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
        getCurrentStudyComponent()
                .ifPresent(study -> {
                    ObservableStudy data = study.getData();
                    if (data != null) {
                        ObservablePublication publication = data.getPublication();
                        if (publication != null) {
                            publicationBrowser.browse(publication.getPmid());
                        } else {
                            // TODO - handle?
                        }
                    } else {
                        // TODO - handle?
                    }
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
            // TODO - implement
//            loader.setControllerFactory(controllerFactory);

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

    /* ************************************************************************************************************** */

    private <T extends ObservableStudy> void addStudy(StudyWrapper<T> wrapper) {
        BaseStudyComponent<T> component = wrapper.component();
        // TODO - bind functional properties to the component.
        component.hpoProperty().bind(optionalResources.hpoProperty());

        Tab tab = new Tab();
        tab.setContent(component);
        tab.textProperty().bind(selectString(component.dataProperty(), "id"));

        tab.setOnCloseRequest(e -> removeStudy(studiesTabPane.getTabs().indexOf(tab)));

        studiesTabPane.getTabs().add(tab);
        studiesTabPane.getSelectionModel().select(tab);
        wrappers.add(wrapper);
    }

    private void removeStudy(int tabIdx) {
        studiesTabPane.getTabs().remove(tabIdx);
        wrappers.remove(tabIdx);
    }

    private Optional<? extends Study> readStudy(Path studyFile) {
        Study study = null;

        // try v1 first
        try {
            DiseaseCase dc = ModelParsers.V1.jsonParser().read(studyFile);
            Ontology hpo = optionalResources.getHpo();
            if (hpo == null) {
                boolean shouldProceed = Dialogs.getBooleanFromUser("Info",
                                "HPO has not been set or loaded",
                                "This will lead to incomplete loading of the data. Set HPO to fix the issue.\nDo you want to continue?")
                        .filter(bt -> bt.equals(ButtonType.OK))
                        .isPresent();
                if (!shouldProceed)
                    return Optional.empty();
            }
            study = ConversionCodecs.v1ToV2(hpo).encode(dc);
        } catch (InvalidProtocolBufferException pbe) {
            // The following holds if we're trying to read v2 model.
            if (!pbe.getMessage().contains("Cannot find field: id in message org.monarchinitiative.hpo_case_annotator_model.proto.DiseaseCase")) {
                Dialogs.showWarningDialog("Error", String.format("Unable to read study at '%s'", studyFile.toAbsolutePath()), SEE_LOG_FOR_MORE_DETAILS);
                LOGGER.warn("Unable to read study at '{}'", studyFile.toAbsolutePath(), pbe);
            }
        } catch (IOException | ModelTransformationException ex) {
            Dialogs.showWarningDialog("Error", String.format("Unable to read study at '%s'", studyFile.toAbsolutePath()), SEE_LOG_FOR_MORE_DETAILS);
            LOGGER.warn("Unable to read study at '{}'", studyFile.toAbsolutePath(), ex);
        }

        // then try v2
        if (study == null) {
            try {
                study = ModelParsers.V2.jsonParser().read(studyFile);
            } catch (IOException ex) {
                Dialogs.showWarningDialog("Error", "Error reading v2 case", SEE_LOG_FOR_MORE_DETAILS);
                LOGGER.warn("Error reading v2 study at '{}'", studyFile.toAbsolutePath(), ex);
                return Optional.empty();
            }
        }

        if (study == null) {
            Dialogs.showWarningDialog("Sorry", "Unable to read study data", "Unknown format");
        }

        return Optional.ofNullable(study);
    }

    private static Optional<StudyWrapper<? extends ObservableStudy>> wrapStudy(Study study, Path path) {
        if (study instanceof IndividualStudy is) {
            IndividualStudyComponent component = new IndividualStudyComponent();
            component.setData(new ObservableIndividualStudy(is));
            return Optional.of(StudyWrapper.of(component, path));
        } else if (study instanceof CohortStudy cs) {
            CohortStudyComponent component = new CohortStudyComponent();
            component.setData(new ObservableCohortStudy(cs));
            return Optional.of(StudyWrapper.of(component, path));
        } else if (study instanceof FamilyStudy fs) {
            FamilyStudyComponent component = new FamilyStudyComponent();
            component.setData(new ObservableFamilyStudy(fs));
            return Optional.of(StudyWrapper.of(component, path));
        } else {
            return Optional.empty();
        }
    }

    private Window getOwnerWindow() {
        return studiesTabPane.getScene().getWindow();
    }

    private boolean saveStudy(Object data, Path path) {
        return convertToStudy(data)
                .map(study -> saveV2Study(study, path))
                .orElse(false);
    }

    private static Optional<Study> convertToStudy(Object data) {
//        if (data instanceof DiseaseCase dc) {
//            try {
//                return Optional.of(ConversionCodecs.v1ToV2().encode(dc));
//            } catch (ModelTransformationException e) {
//                LOGGER.warn("Error serializing study: {}", e.getMessage(), e);
//                return Optional.empty();
//            }
//        } else
        if (data instanceof ObservableStudy study) {
            return Optional.of(study);
        } else {
            LOGGER.warn("Unknown study class: `{}`", data.getClass());
            return Optional.empty();
        }
    }

    private boolean saveV2Study(Study study, Path path) {
        if (path == null) {
            path = askForPath(study.getId());
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

    private static Function<StudyWrapper<? extends ObservableStudy>, Optional<StudyWrapper<? extends ObservableStudy>>> cloneStudyWrapper() {
        return wrapper -> {
            BaseStudyComponent<? extends ObservableStudy> component = wrapper.component();
            if (component == null) {
                return Optional.empty();
            }

            ObservableStudy source = component.getData();
            if (source == null) {
                return Optional.empty();
            }

            Study cloned = null;
            if (source instanceof ObservableIndividualStudy is) {
                cloned = new ObservableIndividualStudy(is);
            } else if (source instanceof ObservableFamilyStudy fs) {
                cloned = new ObservableFamilyStudy(fs);
            } else if (source instanceof ObservableCohortStudy cs) {
                cloned = new ObservableCohortStudy(cs);
            }
            if (cloned == null)
                return Optional.empty();

            // Path must be null to not overwrite the original file
            return wrapStudy(cloned, null);
        };
    }

    private Optional<BaseStudyComponent<?>> getCurrentStudyComponent() {
        return getCurrentStudyWrapper()
                .map(StudyWrapper::component);
    }

    private Optional<StudyWrapper<? extends ObservableStudy>> getCurrentStudyWrapper() {
        if (!studiesTabPane.getSelectionModel().isEmpty()) {
            int selectedStudyTabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
            return Optional.of(wrappers.get(selectedStudyTabIdx));
        }
        return Optional.empty();
    }

//    private <T extends BaseObservableIndividual> void editDisease(ObjectBinding<T> individual) {
//        try {
//            FXMLLoader loader = new FXMLLoader(DiseaseStatusController.class.getResource("DiseaseStatus.fxml"));
//            loader.setControllerFactory(controllerFactory);
//            Parent parent = loader.load();
//
//            // setup & bind controller
//            DiseaseStatusController<T> controller = loader.getController();
//            controller.dataProperty().bind(individual);
//
//            // show the phenotype stage
//            Stage stage = new Stage();
//            stage.initStyle(StageStyle.DECORATED);
//            stage.initOwner(getOwnerWindow());
//            stage.setTitle(String.format("Disease data of %s", individual.get().getId()));
//            stage.setScene(new Scene(parent));
//            stage.showAndWait();
//
//            // unbind
//            controller.dataProperty().unbind();
//        } catch (IOException e) {
//            Dialogs.showErrorDialog("Error", "Error occurred while adding phenotype features", SEE_LOG_FOR_MORE_DETAILS);
//            LOGGER.warn("Error adding phenotype features: {}", e.getMessage(), e);
//        }
//    }
//
//    private Function<Object, Optional<? extends ObjectBinding<? extends BaseObservableIndividual>>> getBaseObservableIndividualBinding() {
//        return data -> {
//            Tab selectedStudyTab = studiesTabPane.getSelectionModel().getSelectedItem();
//            if (data instanceof ObservableFamilyStudy || data instanceof ObservableCohortStudy) {
//                TabPane membersTabPane = (TabPane) selectedStudyTab.getContent().getParent().lookup("#members-tab-pane");
//                Integer tabIdx = membersTabPane.getSelectionModel().selectedIndexProperty().getValue();
//
//                if (tabIdx == null || tabIdx == 0) {
//                    // No tab is open, or summary tab is open.
//                    Dialogs.showWarningDialog("Sorry", "Unable to open the dialog when no individual tab is open", "Open a tab in the pedigree/cohort");
//                    return Optional.empty();
//                }
//
//
//                if (data instanceof ObservableFamilyStudy study) {
//                    return Optional.of(Bindings.valueAt(study.getPedigree().membersProperty(), tabIdx - 1));
//                } else {
//                    return Optional.of(Bindings.valueAt(((ObservableCohortStudy) data).membersProperty(), tabIdx - 1));
//                }
//            } else {
//                Dialogs.showInfoDialog("Sorry", String.format("Working with '%s' is not yet implemented", data.getClass().getName()), null);
//                return Optional.empty();
//            }
//        };
//    }
//
//    private <T extends BaseObservableIndividual> void editPhenotypeFeatures(ObjectBinding<T> individual) {
//        try {
//            FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeBrowser.fxml"));
//            loader.setControllerFactory(controllerFactory);
//            Parent parent = loader.load();
//
//            // setup & bind controller
//            PhenotypeBrowserController<T> controller = loader.getController();
//            controller.ontologyProperty().bind(optionalResources.ontologyProperty());
//            controller.dataProperty().bind(individual);
//
//            // show the phenotype stage
//            Stage stage = new Stage();
//            stage.initStyle(StageStyle.DECORATED);
//            stage.initOwner(getOwnerWindow());
//            stage.setTitle(String.format("Phenotype features of %s", individual.get().getId()));
//            stage.setScene(new Scene(parent));
//            stage.showAndWait();
//
//            // unbind
//            controller.ontologyProperty().unbind();
//            controller.dataProperty().unbind();
//        } catch (IOException e) {
//            Dialogs.showErrorDialog("Error", "Error occurred while adding phenotype features", SEE_LOG_FOR_MORE_DETAILS);
//            LOGGER.warn("Error adding phenotype features: {}", e.getMessage(), e);
//        }
//    }
//
//    private static Optional<StudyType> studyTypeForData(Object data) {
//        if (data instanceof FamilyStudy) {
//            return Optional.of(StudyType.FAMILY);
//        } else if (data instanceof CohortStudy) {
//            return Optional.of(StudyType.COHORT);
//        } else {
//            LOGGER.warn("Unknown study data class: {}", data.getClass());
//            return Optional.empty();
//        }
//    }

}
