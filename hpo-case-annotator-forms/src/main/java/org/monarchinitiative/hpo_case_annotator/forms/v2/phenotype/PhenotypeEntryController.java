package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhenotypeEntryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeEntryController.class);
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    private final PhenotypeSuggestionProvider suggestionProvider = new PhenotypeSuggestionProvider();

    @FXML
    private VBox ontologyTreeBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private VBox ontologyTreeBrowser;
    @FXML
    private OntologyTreeBrowserController ontologyTreeBrowserController;
    @FXML
    private Button addButton;
    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;
    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;
    @FXML
    private Button removeButton;

    @FXML
    private void initialize() {
        suggestionProvider.ontologyProperty().bind(ontology);
        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
        ontologyTreeBox.disableProperty().bind(ontology.isNull());

        phenotypicFeatureController.dataProperty().bind(phenotypicFeaturesTableController.selectedPhenotypeDescription());
        phenotypicFeaturesTableController.selectedPhenotypeDescription().addListener(ontologyTreeBrowserController.phenotypeDescriptionChangeListener());
        phenotypicFeatureController.ontologyProperty().bind(ontology);

        TextFields.bindAutoCompletion(searchTextField, suggestionProvider);
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

    public ObservableList<ObservablePhenotypicFeature> observablePhenotypicFeatures() {
        return phenotypicFeaturesTableController.observablePhenotypeDescriptions();
    }

    @FXML
    private void addButtonAction() {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            return;

        TermId termId = ontologyTreeBrowserController.getSelectedTermId();
        ObservablePhenotypicFeature feature = new ObservablePhenotypicFeature();
        feature.setTermId(termId);
        phenotypicFeaturesTableController.observablePhenotypeDescriptions()
                .add(feature);
    }

    @FXML
    private void removeButtonAction() {
        ObservableList<Integer> selectedIndices = phenotypicFeaturesTableController.getSelectionModel().getSelectedIndices();
        for (int idx : selectedIndices) {
            phenotypicFeaturesTableController.observablePhenotypeDescriptions().remove(idx);
        }
    }

    @FXML
    private void searchTextFieldAction() {
        String text = searchTextField.getText();
        LOGGER.info("Searching for `{}`", text);
        suggestionProvider.termIdForName(text)
                .ifPresent(termId -> ontologyTreeBrowserController.navigateToTermId(termId));
    }
}
