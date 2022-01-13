package org.monarchinitiative.hpo_case_annotator.app.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
    private void newMenuItemAction() {
        ChoiceDialog<StudyType> dialog = new ChoiceDialog<>(StudyType.FAMILY, StudyType.values());
        dialog.setTitle("Choose a study type");
        Optional<StudyType> studyType = dialog.showAndWait();

        studyType.flatMap(Main::controllerUrlForStudyType)
                .ifPresent(url -> addStudy(url, StudyWrapper.of(studyForStudyType(studyType.get()))));
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
    private void openMenuItemAction() {
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
                    study = ConversionCodecs.diseaseCaseStudyCodec().encode(dc);
                } catch (IOException | ModelTransformationException e) {
                    Dialogs.showException("Error", "Error reading v1 case", e.getMessage(), e);
                    continue;
                }
            } else if (selectedFilter.equals(v2Json)) {
                try {
                    study = v2Parser.read(file.toPath());
                } catch (IOException e) {
                    Dialogs.showException("Error", "Error reading v2 case", e.getMessage(), e);
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
    private void saveMenuItemAction() {
        int tabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.study(), wrapper.studyPath());
    }

    private boolean saveStudy(Object data, Path path) {
        return convertToStudy(data)
                .map(study -> saveV2Study(study, path))
                .orElse(false);
    }

    private static Optional<Study> convertToStudy(Object data) {
        if (data instanceof DiseaseCase dc) {
            try {
                return Optional.of(ConversionCodecs.diseaseCaseStudyCodec().encode(dc));
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
    private void saveAsMenuItemAction() {
        int tabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(tabIdx);
        saveStudy(wrapper.study(), null);
    }

    @FXML
    private void saveAllMenuItemAction() {
        for (StudyWrapper<?> wrapper : wrappers) {
            if (!saveStudy(wrapper.study(), wrapper.studyPath())) {
                // user cancelled
                break;
            }
        }
    }


    @FXML
    private void exportToCSVMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void exportToSummaryFileMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void exportPhenopacketCurrentCaseMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void exportPhenopacketAllCasesMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void setResourcesMenuItemAction() {
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
        } catch (IOException e) {
            LOGGER.warn("Error setting up resources: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void exitMenuItemAction() {
        Platform.exit();
    }

    /*                                                 VIEW                                                           */

    @FXML
    private void cloneCaseMenuItemAction() {
        int index = contentTabPane.getSelectionModel().getSelectedIndex();
        StudyWrapper<?> wrapper = wrappers.get(index);
        Optional<StudyType> studyType = studyTypeForData(wrapper.study());

        if (wrapper.study() instanceof ObservableStudy s) {
            studyType.flatMap(Main::controllerUrlForStudyType)
                    .ifPresent(url -> addStudy(url, StudyWrapper.of(s)));
        }
        // TODO - make it work for all study/disease case types

    }

    @FXML
    private void viewOnPubmedMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    /*                                                 PROJECT                                                        */

    @FXML
    private void showCuratedPublicationsMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void showCuratedVariantsMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    /*                                                 GENOTYPE                                                       */



    /*                                                 PHENOTYPE                                                      */

    @FXML
    private void addEditPhenotypicFeaturesMenuItemAction() {
        int selectedTabIdx = contentTabPane.getSelectionModel().getSelectedIndex();
        Tab tab = contentTabPane.getSelectionModel().getSelectedItem();

        Object study = wrappers.get(selectedTabIdx).study();
        if (study instanceof ObservableFamilyStudy ofs) {
            TabPane familyTabPane = (TabPane) tab.getContent().getParent().lookup("#family-tab-pane");
            int tabIdx = familyTabPane.getSelectionModel().getSelectedIndex();
            List<ObservablePedigreeMember> members = ofs.getPedigree().members();
            ObservablePedigreeMember member = members.get(tabIdx - 1);
            editPhenotypeFeatures(member);
        }
    }

    private <T extends BaseObservableIndividual> void editPhenotypeFeatures(T individual) {
        try {
            FXMLLoader loader = new FXMLLoader(PhenotypeBrowserController.class.getResource("PhenotypeBrowser.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            PhenotypeBrowserController controller = loader.getController();
            controller.ontologyProperty().bind(optionalResources.ontologyProperty());
            Bindings.bindContentBidirectional(controller.phenotypeDescriptions(), individual.phenotypicFeatures());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.DECORATED);
            stage.initOwner(contentTabPane.getScene().getWindow());
            stage.setTitle("Add / edit phenotype features");
            stage.setScene(new Scene(parent));
            stage.showAndWait();

            controller.ontologyProperty().unbind();
            Bindings.unbindContentBidirectional(controller.phenotypeDescriptions(), individual.phenotypicFeatures());
        } catch (IOException e) {
            LOGGER.warn("Error adding phenotype features: {}", e.getMessage());
        }
    }

    /*                                                 DISEASE                                                        */

    @FXML
    private void addEditDiseaseMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    /*                                                 VALIDATE                                                       */
    @FXML
    private void validateCurrentEntryMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void validateAllModelsMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    /*                                                 HELP                                                           */

    @FXML
    private void helpMenuItemAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }

    @FXML
    private void liftoverAction() {
        // TODO - implement
        Dialogs.showInfoMessage("Sorry", "Not yet implemented");
    }
}
