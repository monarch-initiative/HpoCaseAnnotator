package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.v2.AgeController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.forms.v2.observable.ObservablePhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.phenol.ontology.data.Ontology;
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
    private HBox onsetAge;
    @FXML
    private AgeController onsetAgeController;
    @FXML
    private HBox resolutionAge;
    @FXML
    private AgeController resolutionAgeController;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;
    @FXML
    private Button removeButton;

    @FXML
    private Button okButton;

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

    @FXML
    private void addButtonAction() {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            return;

        ObservablePhenotypicFeature feature = new ObservablePhenotypicFeature();
        feature.setTermId(ontologyTreeBrowserController.getSelectedTermId());
        copyObservableAge(onsetAgeController.getData(), feature.getObservationAge().getOnset());
        copyObservableAge(resolutionAgeController.getData(), feature.getObservationAge().getResolution());
        phenotypicFeaturesTableController.observablePhenotypeDescriptions()
                .add(feature);
    }

    private static void copyObservableAge(ObservableAge source, ObservableAge target) {
        target.setYears(source.getYears());
        target.setMonths(source.getMonths());
        target.setDays(source.getDays());
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

    @FXML
    private void okButtonAction(ActionEvent e) {
        // close the stage
        Stage stage = (Stage) okButton.getParent().getScene().getWindow();
        stage.close();
        e.consume();
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

    public ObjectProperty<ObservableAge> onsetAge() {
        return onsetAgeController.dataProperty();
    }

    public ObjectProperty<ObservableAge> resolutionAge() {
        return resolutionAgeController.dataProperty();
    }

}
