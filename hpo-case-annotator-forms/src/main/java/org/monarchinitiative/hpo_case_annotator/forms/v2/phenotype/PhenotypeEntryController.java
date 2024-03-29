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
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeSuggestionProvider;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.forms.v2.AgeController;
import org.monarchinitiative.hpo_case_annotator.forms.tree.SimpleOntologyClassTreeView;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
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
    private SimpleOntologyClassTreeView ontologyTreeView;
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
    private void initialize() {
        suggestionProvider.ontologyProperty().bind(ontology);
        ontologyTreeView.ontologyProperty().bind(ontology);
        ontologyTreeBox.disableProperty().bind(ontology.isNull());

        phenotypicFeatureController.dataProperty().bind(phenotypicFeaturesTableController.selectedPhenotypeDescription());
        // TODO - Fix if necessary
//        phenotypicFeaturesTableController.selectedPhenotypeDescription().addListener(ontologyTreeBrowserController.phenotypeDescriptionChangeListener());
        phenotypicFeatureController.ontologyProperty().bind(ontology);

        TextFields.bindAutoCompletion(searchTextField, suggestionProvider);

        removeButton.disableProperty().bind(phenotypicFeaturesTableController.selectedPhenotypeDescription().isNull());
    }

    @FXML
    private void addButtonAction() {
        Ontology ontology = this.ontology.get();
        if (ontology == null)
            return;

        ObservablePhenotypicFeature feature = new ObservablePhenotypicFeature();
        feature.setTermId(ontologyTreeView.selectedItemProperty().get().getValue().getId());
        // TODO - implement or delete the entire class.
//        copyObservableAge(onsetAgeController.getData(), feature.getObservationAge().getOnset());
//        copyObservableAge(resolutionAgeController.getData(), feature.getObservationAge().getResolution());
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
        ObservableList<ObservablePhenotypicFeature> selectedIndices = phenotypicFeaturesTableController.getSelectionModel().getSelectedItems();
        phenotypicFeaturesTableController.observablePhenotypeDescriptions().removeAll(selectedIndices);
    }

    @FXML
    private void searchTextFieldAction() {
        String text = searchTextField.getText();
        LOGGER.debug("Searching for `{}`", text);
        suggestionProvider.ontologyClassForLabel(text)
                .ifPresent(oc -> ontologyTreeView.scrollTo(oc));
    }

    @FXML
    private void okButtonAction(ActionEvent e) {
        Utils.closeTheStage(e);
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
