package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.hpo_case_annotator.app.model.OptionalResources;
import org.monarchinitiative.hpo_case_annotator.app.StudyType;
import org.monarchinitiative.hpo_case_annotator.convert.ConversionCodecs;
import org.monarchinitiative.hpo_case_annotator.convert.ModelTransformationException;
import org.monarchinitiative.hpo_case_annotator.forms.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.CohortStudyController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.FamilyStudyController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.StudyController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.*;
import org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype.PhenotypeBrowserController;
import org.monarchinitiative.hpo_case_annotator.io.ModelParser;
import org.monarchinitiative.hpo_case_annotator.io.ModelParsers;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
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

@Component
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final OptionalResources optionalResources;
    private final HCAControllerFactory controllerFactory;

    /**
     * This list contains data wrappers in the same order as they are present in the {@link #contentTabPane}.
     * <p>
     * The only place where items are added into this list is the {@link #addStudy(URL, StudyWrapper)} method.
     */
    private final List<StudyWrapper<?>> wrappers = new LinkedList<>();
    public MenuItem addEditDiseaseMenuItem;

    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem cloneCaseMenuItem;
    @FXML
    private MenuItem viewOnPubmedMenuItem;
    @FXML
    private MenuItem showCuratedPublicationsMenuItem;
    @FXML
    private MenuItem addEditPhenotypeFeaturesMenuItem;
    @FXML
    private MenuItem showCuratedVariantsMenuItem;
    @FXML
    private MenuItem validateCurrentEntryMenuItem;
    @FXML
    private MenuItem exportPhenopacketMenuItem;

    @FXML
    private Menu genotypeMenu;
    @FXML
    private Menu phenotypeMenu;
    @FXML
    private Menu diseaseMenu;

    @FXML
    private TabPane contentTabPane;

    @FXML
    private HBox statusBar;

    @FXML
    private StatusBarController statusBarController;

    public Main(OptionalResources optionalResources,
                HCAControllerFactory controllerFactory) {
        this.optionalResources = optionalResources;
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        disableMenuEntriesDependentOnADataModel();
    }

    private void disableMenuEntriesDependentOnADataModel() {
        BooleanBinding noTabIsPresent = contentTabPane.getSelectionModel().selectedItemProperty().isNull();
        saveMenuItem.disableProperty().bind(noTabIsPresent);
        saveAsMenuItem.disableProperty().bind(noTabIsPresent);
        saveAllMenuItem.disableProperty().bind(noTabIsPresent);
        cloneCaseMenuItem.disableProperty().bind(noTabIsPresent);
        viewOnPubmedMenuItem.disableProperty().bind(noTabIsPresent);
        validateCurrentEntryMenuItem.disableProperty().bind(noTabIsPresent);
        genotypeMenu.disableProperty().bind(noTabIsPresent);
        phenotypeMenu.disableProperty().bind(noTabIsPresent);
        diseaseMenu.disableProperty().bind(noTabIsPresent);
    }

    /*                                                 FILE                                                           */

    @FXML
    private void newMenuItemAction(ActionEvent e) {
        var individualStudy = new CommandLinksDialog.CommandLinksButtonType("Individual study", "Enter data about an individual (a cohort with size 1)", false);
        var familyStudy = new CommandLinksDialog.CommandLinksButtonType("Family study", "Enter data about several related individuals", false);
        var cohortStudy = new CommandLinksDialog.CommandLinksButtonType("Cohort study", "Enter data about several unrelated individuals", false);
        CommandLinksDialog dialog = new CommandLinksDialog(individualStudy, familyStudy, cohortStudy);
        dialog.setTitle("New study");
        dialog.setHeaderText("Select study type");
        dialog.setContentText("HCA supports biocuration of several study types");
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
            tab.setOnCloseRequest(e -> removeStudy(contentTabPane.getTabs().indexOf(tab)));

            contentTabPane.getTabs().add(tab);
            wrappers.add(wrapper);
        } catch (IOException e) {
            LOGGER.warn("Error loading study: {}", e.getMessage(), e);
        }
    }

    private void removeStudy(int tabIdx) {
        contentTabPane.getTabs().remove(tabIdx);
        wrappers.remove(tabIdx);
    }

    @FXML
    private void openMenuItemAction(ActionEvent e) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(optionalResources.getDiseaseCaseDir());
        filechooser.setTitle("Open study");

        String HCA_v1_JSON = "HCA v1 JSON data format (*.json)";
        String HCA_v2_JSON = "HCA v2 JSON data format (*.json)";
        FileChooser.ExtensionFilter v1Json = new FileChooser.ExtensionFilter(HCA_v1_JSON, "*.json");
        FileChooser.ExtensionFilter v2Json = new FileChooser.ExtensionFilter(HCA_v2_JSON, "*.json");
        filechooser.getExtensionFilters().addAll(v1Json, v2Json);
        filechooser.setSelectedExtensionFilter(v1Json);

        List<File> files = filechooser.showOpenMultipleDialog(contentTabPane.getScene().getWindow());

        if (files == null)
            return;

        FileChooser.ExtensionFilter selectedFilter = filechooser.getSelectedExtensionFilter();

        ModelParser<Study> v2Parser = ModelParsers.V2.jsonParser();
        ModelParser<DiseaseCase> v1Parser = ModelParsers.V1.jsonParser();
        for (File file : files) {
            Study study;
            if (selectedFilter.equals(v1Json)) {
                // v1
                try {
                    DiseaseCase dc = v1Parser.read(file.toPath());
                    study = ConversionCodecs.v1ToV2().encode(dc);
                } catch (IOException | ModelTransformationException ex) {
                    Dialogs.showException("Error", "Error reading v1 case", ex.getMessage(), ex);
                    continue;
                }
            } else if (selectedFilter.equals(v2Json)) {
                try {
                    study = v2Parser.read(file.toPath());
                } catch (IOException ex) {
                    Dialogs.showException("Error", "Error reading v2 case", ex.getMessage(), ex);
                    continue;
                }
            } else {
                Dialogs.showWarningDialog("Sorry", "Sorry", "Unexpected model version");
                continue;
            }

            Convert.toObservableStudy(study)
                    .ifPresent(observableStudy -> studyTypeForStudy(observableStudy)
                            .flatMap(Main::controllerUrlForStudyType)
                            .ifPresent(url -> addStudy(url, StudyWrapper.of(observableStudy, file.toPath())))
                    );
        }
        e.consume();
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

    @FXML
    private void saveMenuItemAction(ActionEvent e) {
        int tabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
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
        File which = fileChooser.showSaveDialog(contentTabPane.getScene().getWindow());

        return (which == null)
                ? null
                : which.toPath();
    }

    @FXML
    private void saveAsMenuItemAction(ActionEvent e) {
        int tabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
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
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportToCSVMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportToSummaryFileMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportPhenopacketCurrentCaseMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void exportPhenopacketAllCasesMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
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
            stage.initOwner(contentTabPane.getScene().getWindow());
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
        Platform.exit();
        e.consume();
    }

    @FXML
    private void cloneCaseMenuItemAction(ActionEvent e) {
        int index = contentTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(index);
        Optional<StudyType> studyType = studyTypeForData(wrapper.study());

        if (wrapper.study() instanceof ObservableStudy s) {
            studyType.flatMap(Main::controllerUrlForStudyType)
                    .ifPresent(url -> addStudy(url, StudyWrapper.of(s)));
        }
        // TODO - make it work for all study/disease case types
        e.consume();

    }

    /*                                                 VIEW                                                           */

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

    @FXML
    private void viewOnPubmedMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    /*                                                 PROJECT                                                        */

    @FXML
    private void showCuratedPublicationsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void showCuratedVariantsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    /*                                                 GENOTYPE                                                       */



    /*                                                 PHENOTYPE                                                      */

    @FXML
    private void addEditPhenotypicFeaturesMenuItemAction(ActionEvent e) {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        Tab tab = contentTabPane.getSelectionModel().getSelectedItem();

        Object study = wrappers.get(selectedTabIdx).study();
        if (study instanceof ObservableFamilyStudy ofs) {
            TabPane familyTabPane = (TabPane) tab.getContent().getParent().lookup("#family-tab-pane");
            int tabIdx = familyTabPane.getSelectionModel().getSelectedIndex();
            ObservableList<ObservablePedigreeMember> members = ofs.getPedigree().members();
            ObjectBinding<ObservablePedigreeMember> member = Bindings.valueAt(members, tabIdx - 1);
            editPhenotypeFeatures(member);
        }
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
            stage.initOwner(contentTabPane.getScene().getWindow());
            stage.setTitle("Add / edit phenotype features");
            stage.setScene(new Scene(parent));
            stage.showAndWait();

            // unbind
            controller.ontologyProperty().unbind();
            controller.dataProperty().unbind();
        } catch (IOException e) {
            LOGGER.warn("Error adding phenotype features: {}", e.getMessage());
        }
    }

    /*                                                 DISEASE                                                        */

    @FXML
    private void addEditDiseaseMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    /*                                                 VALIDATE                                                       */
    @FXML
    private void validateCurrentEntryMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void validateAllModelsMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    /*                                                 HELP                                                           */

    @FXML
    private void helpMenuItemAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }

    @FXML
    private void liftoverAction(ActionEvent e) {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
        e.consume();
    }
}
