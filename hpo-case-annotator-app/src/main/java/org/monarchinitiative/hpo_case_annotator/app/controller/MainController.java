package org.monarchinitiative.hpo_case_annotator.app.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.hpo_case_annotator.app.App;
import org.monarchinitiative.hpo_case_annotator.app.dialogs.Dialogs;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.StudyType;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalServices;
import org.monarchinitiative.hpo_case_annotator.app.publication.PublicationBrowser;
import org.monarchinitiative.hpo_case_annotator.convert.ConversionCodecs;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResources;
import org.monarchinitiative.hpo_case_annotator.forms.StudyResourcesAware;
import org.monarchinitiative.hpo_case_annotator.forms.liftover.Liftover;
import org.monarchinitiative.hpo_case_annotator.forms.status.StatusBar;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.*;
import org.monarchinitiative.hpo_case_annotator.forms.study.BaseStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.study.CohortStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.study.FamilyStudyComponent;
import org.monarchinitiative.hpo_case_annotator.forms.study.IndividualStudyComponent;
import org.monarchinitiative.hpo_case_annotator.model.convert.ModelTransformationException;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static javafx.beans.binding.Bindings.*;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String SEE_LOG_FOR_MORE_DETAILS = "See log for more details.";

    private final ControllerFactory controllerFactory;
    private final OptionalResources optionalResources;
    private final OptionalServices optionalServices;
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
    @FXML
    private MenuItem liftoverMenuItem;

    /* ************************************************   THE REST   ************************************************* */


    @FXML
    private TabPane studiesTabPane;

    @FXML
    private StatusBar statusBar;

    public MainController(ControllerFactory controllerFactory,
                          OptionalResources optionalResources,
                          OptionalServices optionalServices,
                          PublicationBrowser publicationBrowser) {
        this.controllerFactory = controllerFactory;
        this.optionalResources = optionalResources;
        this.optionalServices = optionalServices;
        this.publicationBrowser = publicationBrowser;
    }

    @FXML
    private void initialize() {
        disableMenuEntriesDependentOnADataModel();

        liftoverMenuItem.disableProperty().bind(optionalServices.liftoverServiceProperty().isNull());
        statusBar.messageProperty().bind(optionalResources.statusBinding());
    }

    private void disableMenuEntriesDependentOnADataModel() {
        BooleanBinding noTabIsPresent = studiesTabPane.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding biocuratorIsNullOrEmpty = optionalResources.biocuratorIdProperty().isNull().or(optionalResources.biocuratorIdProperty().isEmpty());
        BooleanBinding savingIsDisabled = noTabIsPresent.or(biocuratorIsNullOrEmpty);
        saveMenuItem.disableProperty().bind(savingIsDisabled);
        saveAsMenuItem.disableProperty().bind(savingIsDisabled);
        saveAllMenuItem.disableProperty().bind(savingIsDisabled);
        closeMenuItem.disableProperty().bind(noTabIsPresent);

        cloneCaseMenuItem.disableProperty().bind(noTabIsPresent);
        studyMenu.disableProperty().bind(noTabIsPresent);
        viewOnPubmedMenuItem.disableProperty().bind(noTabIsPresent);

        validateCurrentEntryMenuItem.disableProperty().bind(noTabIsPresent);
    }

    /*                                                 FILE                                                           */

    @FXML
    private void newMenuItemAction(ActionEvent e) {
        var individualStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.INDIVIDUAL.getName(), StudyType.INDIVIDUAL.getDescription(), true);
        var familyStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.FAMILY.getName(), StudyType.FAMILY.getDescription(), false);
        // TODO - enable once we have Cohort Stepper
//        var cohortStudy = new CommandLinksDialog.CommandLinksButtonType(StudyType.COHORT.getName(), StudyType.COHORT.getDescription(), false);

        CommandLinksDialog dialog = new CommandLinksDialog(individualStudy,
                familyStudy
//                ,cohortStudy
        );

        dialog.setTitle("New study");
        dialog.setHeaderText("Select study type");
        dialog.setContentText("HCA supports the following study types:");
        Optional<ButtonType> buttonType = dialog.showAndWait();

        buttonType.flatMap(bt -> {
            if (bt.equals(individualStudy.getButtonType())) {
                return Optional.of(new ObservableIndividualStudy());
            } else if (bt.equals(familyStudy.getButtonType())) {
                return Optional.of(new ObservableFamilyStudy());
                // TODO - enable once we have Cohort Stepper
//            } else if (bt.equals(cohortStudy.getButtonType())) {
//                return Optional.of(new ObservableCohortStudy());
            } else {
                return Optional.empty();
            }
        }).flatMap(displayStepper())
                .flatMap(st -> wrapStudy(st, null))
                .ifPresent(this::addStudy);
        e.consume();
    }

    @FXML
    private void openMenuItemAction(ActionEvent e) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir().toFile());
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
        saveAsV2Study(wrapper.component().getData(), wrapper.studyPath());
        e.consume();
    }

    @FXML
    private void saveAsMenuItemAction(ActionEvent e) {
        int tabIdx = studiesTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        ObservableStudy study = wrapper.component().getData();
        saveAsV2Study(study, null);
        e.consume();
    }

    @FXML
    private void saveAllMenuItemAction(ActionEvent e) {
        for (StudyWrapper<?> wrapper : wrappers) {
            if (!saveAsV2Study(wrapper.component().getData(), wrapper.studyPath())) {
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
            FXMLLoader loader = new FXMLLoader(SettingsController.class.getResource("Settings.fxml"));
            loader.setControllerFactory(controllerFactory);

            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(getOwnerWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Settings");
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(App.BASE_CSS);
            stage.setScene(scene);
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
        Liftover liftover = new Liftover();
        liftover.liftoverServiceProperty().bind(optionalServices.liftoverServiceProperty());

        Stage stage = new Stage();
        stage.initOwner(getOwnerWindow());
        stage.initModality(Modality.NONE);
        stage.setTitle("Liftover a contig position");
        Scene scene = new Scene(liftover, 500, 400);
        scene.getStylesheets().add(App.BASE_CSS);
        stage.setScene(scene);
        stage.show();

        e.consume();
    }

    /*                                                 OTHERS                                                         */

    /* ************************************************************************************************************** */

    private <T extends ObservableStudy> void addStudy(StudyWrapper<T> wrapper) {
        BaseStudyComponent<T> component = wrapper.component();

        Tab tab = new Tab();
        tab.setContent(component);
        StringBinding idBinding = selectString(component.dataProperty(), "id");
        tab.textProperty().bind(when(idBinding.isNotNull()).then(idBinding).otherwise("N/A"));

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
            Ontology hpo = optionalServices.getHpo();
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

    private Optional<StudyWrapper<? extends ObservableStudy>> wrapStudy(Study study, Path path) {
        if (study instanceof IndividualStudy is) {
            IndividualStudyComponent component = new IndividualStudyComponent();
            component.setData(new ObservableIndividualStudy(is));
            wireFunctionalPropertiesToStudyResourcesAware(component);
            return Optional.of(StudyWrapper.of(component, path));
        } else if (study instanceof CohortStudy cs) {
            CohortStudyComponent component = new CohortStudyComponent();
            component.setData(new ObservableCohortStudy(cs));
            wireFunctionalPropertiesToStudyResourcesAware(component);
            return Optional.of(StudyWrapper.of(component, path));
        } else if (study instanceof FamilyStudy fs) {
            FamilyStudyComponent component = new FamilyStudyComponent();
            component.setData(new ObservableFamilyStudy(fs));
            wireFunctionalPropertiesToStudyResourcesAware(component);
            return Optional.of(StudyWrapper.of(component, path));
        } else {
            return Optional.empty();
        }
    }

    private void wireFunctionalPropertiesToStudyResourcesAware(StudyResourcesAware resourcesAware) {
        StudyResources resources = resourcesAware.getStudyResources();
        resources.hpoProperty().bind(optionalServices.hpoProperty());
        resources.namedEntityFinderProperty().bind(optionalServices.namedEntityFinderProperty());
        resources.diseaseIdentifierServiceProperty().bind(optionalServices.diseaseIdentifierServiceProperty());
        resources.genomicAssemblyRegistryProperty().set(optionalServices.getGenomicAssemblyRegistry());
        resources.functionalAnnotationRegistryProperty().set(optionalServices.getFunctionalAnnotationRegistry());
        resources.liftoverServiceProperty().bind(optionalServices.liftoverServiceProperty());
    }

    private Window getOwnerWindow() {
        return studiesTabPane.getParent().getScene().getWindow();
    }

    private boolean saveAsV2Study(ObservableStudy study, Path path) {
        if (path == null) {
            path = askForPath(study.getId());
        }

        if (path == null) // the user canceled
            return false;

        study.getStudyMetadata().getModifiedBy().add(prepareEditHistoryEntry());

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
        fileChooser.setInitialDirectory(optionalResources.getDiseaseCaseDir().toFile());
        fileChooser.getExtensionFilters().add(jsonFileFormat);
        File which = fileChooser.showSaveDialog(getOwnerWindow());

        return (which == null)
                ? null
                : which.toPath();
    }

    private ObservableEditHistory prepareEditHistoryEntry() {
        ObservableEditHistory entry = new ObservableEditHistory();
        entry.setCuratorId(optionalResources.getBiocuratorId());
        entry.setSoftwareVersion(optionalResources.getSoftwareVersion());
        entry.setTimestamp(Instant.now());
        return entry;
    }

    private Function<StudyWrapper<? extends ObservableStudy>, Optional<StudyWrapper<? extends ObservableStudy>>> cloneStudyWrapper() {
        return wrapper -> Optional.ofNullable(wrapper.component())
                .flatMap(component -> Optional.ofNullable(component.getData()))
                .flatMap(source -> {
                    // Clone source study
                    Study cloned = null;
                    if (source instanceof ObservableIndividualStudy is)
                        cloned = new ObservableIndividualStudy(is);
                    else if (source instanceof ObservableFamilyStudy fs)
                        cloned = new ObservableFamilyStudy(fs);
                    else if (source instanceof ObservableCohortStudy cs)
                        cloned = new ObservableCohortStudy(cs);
                    return Optional.ofNullable(cloned);
                })
                // Path must be null to not overwrite the original file
                .flatMap(cloned -> wrapStudy(cloned, null));
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

    private <T extends ObservableStudy> Function<T, Optional<? extends T>> displayStepper() {
        return study -> {
            Optional<? extends BaseStudySteps<T>> optionalSteps = prepareSteps(study);
            if (optionalSteps.isEmpty())
                return Optional.empty();

            BaseStudySteps<T> steps = optionalSteps.get();
            Stepper<T> stepper = new Stepper<>();
            wireFunctionalPropertiesToStudyResourcesAware(steps);
            stepper.stepsProperty().bind(steps.stepsProperty());
            stepper.setData(study);

            Stage stage = new Stage();
            AtomicBoolean accept = new AtomicBoolean();
            stepper.statusProperty().addListener((obs, old, novel) -> {
                switch (novel) {
                    case IN_PROGRESS -> {
                        return; // Do not close the stage
                    }
                    case FINISH -> accept.set(true);
                    case CANCEL -> accept.set(false);
                }
                stage.close();
            });

            stage.initOwner(getOwnerWindow());
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Enter study details");
            stage.setResizable(true);
            Scene scene = new Scene(stepper);
            scene.getStylesheets().add(App.BASE_CSS);
            stage.setScene(scene);

            stage.showAndWait();

            return accept.get()
                    ? Optional.of(stepper.getData())
                    : Optional.empty();
        };
    }

    private static <T extends ObservableStudy> Optional<? extends BaseStudySteps<T>> prepareSteps(T study) {
        Object steps;
        if (study instanceof ObservableIndividualStudy) {
            steps = new IndividualStudySteps().configureSteps();
        } else if (study instanceof ObservableFamilyStudy) {
            steps = new FamilyStudySteps().configureSteps();
        } else if (study instanceof ObservableCohortStudy) {
            steps = new CohortStudySteps().configureSteps();
        } else {
            LOGGER.warn("Unable to create steps for {}", study.getClass().getSimpleName());
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((BaseStudySteps<T>) steps);
    }

}
