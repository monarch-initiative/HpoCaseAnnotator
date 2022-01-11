package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhenotypeBrowserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeBrowserController.class);

    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    @FXML
    private VBox ontologyTermTreeBrowser;
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

    @FXML
    private void initialize() {
        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
        phenotypicFeatureController.ontologyProperty().bind(ontology);
        phenotypicFeaturesTableController.selectedPhenotypeDescription().addListener(phenotypicFeatureController.phenotypeDescriptionChangeListener());
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
        LOGGER.info("Adding phenotype term");
        // TODO
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
