package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhenotypeBrowserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeBrowserController.class);
    private final HCAControllerFactory controllerFactory;
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    @FXML
    private VBox ontologyTreeBrowser;
    @FXML
    private OntologyTreeBrowserController ontologyTreeBrowserController;
    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;
    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;

    public PhenotypeBrowserController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
        phenotypicFeatureController.ontologyProperty().bind(ontology);
        phenotypicFeatureController.dataProperty().bind(phenotypicFeaturesTableController.selectedPhenotypeDescription());
        phenotypicFeaturesTableController.selectedPhenotypeDescription().addListener(ontologyTreeBrowserController.phenotypeDescriptionChangeListener());

        // phenotypic feature is disabled when no phenotypic feature is selected
        phenotypicFeatureController.disableProperty().bind(phenotypicFeaturesTableController.getSelectionModel().selectedItemProperty().isNull());

        initializeRemoveButton();
    }

    private void initializeRemoveButton() {
        removeButton.disableProperty().bind(phenotypicFeaturesTableController.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void addButtonAction() {
        try {
            // setup loader
            FXMLLoader loader = new FXMLLoader(PhenotypeEntryController.class.getResource("PhenotypeEntry.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // setup controller
            PhenotypeEntryController controller = loader.getController();
            controller.ontologyProperty().bind(ontology);
            Bindings.bindContentBidirectional(controller.observablePhenotypicFeatures(), phenotypicFeaturesTableController.observablePhenotypeDescriptions());

            // setup stage
            Stage stage = new Stage();
            stage.initOwner(ontologyTreeBrowser.getScene().getWindow());
            stage.setTitle("Add phenotype features");
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.DECORATED);
            stage.showAndWait();

            Bindings.unbindContentBidirectional(controller.observablePhenotypicFeatures(), phenotypicFeaturesTableController.observablePhenotypeDescriptions());
        } catch (IOException e) {
            LOGGER.warn("Error while adding phenotype features: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void removeButtonAction() {
        ObservableList<Integer> selectedIndices = phenotypicFeaturesTableController.getSelectionModel().getSelectedIndices();
        for (int idx : selectedIndices) {
            phenotypicFeaturesTableController.observablePhenotypeDescriptions().remove(idx);
        }
    }

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public ObservableList<ObservablePhenotypicFeature> phenotypeDescriptions() {
        return phenotypicFeaturesTableController.observablePhenotypeDescriptions();
    }

}
