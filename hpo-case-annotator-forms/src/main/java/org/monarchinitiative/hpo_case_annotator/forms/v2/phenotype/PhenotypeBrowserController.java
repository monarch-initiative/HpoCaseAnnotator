package org.monarchinitiative.hpo_case_annotator.forms.v2.phenotype;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhenotypeBrowserController<T extends BaseObservableIndividual> extends BindingDataController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeBrowserController.class);
    private final HCAControllerFactory controllerFactory;
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    private final ObjectProperty<T> individual = new SimpleObjectProperty<>(this, "individual");

    /* --------------------------------------------   CONTENT   ----------------------------------------------------- */
    @FXML
    private VBox ontologyTreeBrowser;
    @FXML
    private OntologyTreeBrowserController ontologyTreeBrowserController;

    @FXML
    private Label idLabel;
    @FXML
    private Label sexLabel;
    @FXML
    private Label ageLabel;

    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;

    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;

    @FXML
    private Button addPhenotypeFeatureButton;
    @FXML
    private Button removePhenotypeFeatureButton;

    @FXML
    private Button okButton;

    public PhenotypeBrowserController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    protected void initialize() {
        super.initialize();

        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
        phenotypicFeaturesTableController.selectedPhenotypeDescription().addListener(ontologyTreeBrowserController.phenotypeDescriptionChangeListener());

        phenotypicFeatureController.ontologyProperty().bind(ontology);
        // phenotypic feature is disabled when no phenotypic feature is selected
        phenotypicFeatureController.disableProperty().bind(phenotypicFeaturesTableController.getSelectionModel().selectedItemProperty().isNull());
        phenotypicFeatureController.dataProperty().bind(phenotypicFeaturesTableController.selectedPhenotypeDescription());

        removePhenotypeFeatureButton.disableProperty().bind(phenotypicFeaturesTableController.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    protected void bind(T individual) {
        idLabel.textProperty().bind(individual.idProperty());
        sexLabel.textProperty().bind(individual.sexProperty().asString());
        ageLabel.textProperty().bind(individual.getAge().period().asString());
        Bindings.bindContentBidirectional(phenotypicFeaturesTableController.observablePhenotypeDescriptions(), individual.phenotypicFeatures());
    }

    @Override
    protected void unbind(T individual) {
        idLabel.textProperty().unbind();
        sexLabel.textProperty().unbind();
        ageLabel.textProperty().unbind();
        Bindings.unbindContentBidirectional(phenotypicFeaturesTableController.observablePhenotypeDescriptions(), individual.phenotypicFeatures());
    }

    @FXML
    private void addPhenotypeFeatureButtonAction(ActionEvent e) {
        BaseObservableIndividual individual = this.individual.get();

        try {
            // setup loader
            FXMLLoader loader = new FXMLLoader(PhenotypeEntryController.class.getResource("PhenotypeEntry.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();

            // setup & bind controller
            PhenotypeEntryController controller = loader.getController();
            controller.ontologyProperty().bind(ontology);
            Bindings.bindContentBidirectional(controller.observablePhenotypicFeatures(), phenotypicFeaturesTableController.observablePhenotypeDescriptions());
            // TODO - implement proper visit age form
            ObjectProperty<ObservableAge> visitAge = new SimpleObjectProperty<>(new ObservableAge(0, 0, 0));
            controller.onsetAge().bindBidirectional(visitAge);
            if (individual != null)
                controller.resolutionAge().bindBidirectional(individual.ageProperty());

            // show stage
            Stage stage = new Stage();
            stage.initOwner(ontologyTreeBrowser.getScene().getWindow());
            stage.setTitle("Add phenotype features");
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.DECORATED);
            stage.showAndWait();

            // unbind controlker
            controller.ontologyProperty().unbind();
            Bindings.unbindContentBidirectional(controller.observablePhenotypicFeatures(), phenotypicFeaturesTableController.observablePhenotypeDescriptions());
            controller.onsetAge().unbindBidirectional(visitAge);
            if (individual != null)
                controller.resolutionAge().unbindBidirectional(individual.ageProperty());
        } catch (IOException ex) {
            LOGGER.warn("Error while adding phenotype features: {}", ex.getMessage(), ex);
        }

        e.consume();
    }

    @FXML
    private void removePhenotypeFeatureButtonAction(ActionEvent e) {
        ObservableList<ObservablePhenotypicFeature> selectedItems = phenotypicFeaturesTableController.getSelectionModel().getSelectedItems();
        phenotypicFeaturesTableController.observablePhenotypeDescriptions().removeAll(selectedItems);

        e.consume();
    }

    @FXML
    private void okButtonAction(ActionEvent e) {
        // close the stage
        Stage stage = (Stage) okButton.getParent().getScene().getWindow();
        stage.close();
        e.consume();
    }

    /* --------------------------------------------   PROPERTIES  --------------------------------------------------- */

    public Ontology getOntology() {
        return ontology.get();
    }

    public void setOntology(Ontology ontology) {
        this.ontology.set(ontology);
    }

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    @Override
    public ObjectProperty<T> dataProperty() {
        return individual;
    }
}
