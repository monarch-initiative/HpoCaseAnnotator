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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.util.Utils;
import org.monarchinitiative.hpo_case_annotator.forms.v2.IndividualDetailController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableAge;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PhenotypeBrowserController<T extends BaseObservableIndividual<?>> extends BindingDataController<T> {

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
    private HBox individualDetail;
    @FXML
    private IndividualDetailController individualDetailController;

    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;

    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;

    @FXML
    private Button textMiningButton;
    @FXML
    private Button addPhenotypeFeatureButton;
    @FXML
    private Button removePhenotypeFeatureButton;

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

//        textMiningButton.disableProperty().bind(ontology.isNull());

        removePhenotypeFeatureButton.disableProperty().bind(phenotypicFeaturesTableController.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    protected void bind(T individual) {
        individualDetailController.idProperty().bind(individual.idProperty());
        individualDetailController.sexProperty().bind(individual.sexProperty().asString());
        individualDetailController.ageLabel().bind(individual.getObservableAge().period().asString());
        Bindings.bindContentBidirectional(phenotypicFeaturesTableController.observablePhenotypeDescriptions(), individual.getObservablePhenotypicFeatures());
    }

    @Override
    protected void unbind(T individual) {
        individualDetailController.idProperty().unbind();
        individualDetailController.sexProperty().unbind();
        individualDetailController.ageLabel().unbind();
        Bindings.unbindContentBidirectional(phenotypicFeaturesTableController.observablePhenotypeDescriptions(), individual.getObservablePhenotypicFeatures());
    }

    @FXML
    private void addPhenotypeFeatureButtonAction(ActionEvent e) {
        BaseObservableIndividual<?> individual = this.individual.get();

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
            ObjectProperty<ObservableAge> visitAge = new SimpleObjectProperty<>(new ObservableAge());
            controller.onsetAge().bindBidirectional(visitAge);
            if (individual != null)
                controller.resolutionAge().bindBidirectional(individual.observableAgeProperty());

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
                controller.resolutionAge().unbindBidirectional(individual.observableAgeProperty());
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
        Utils.closeTheStage(e);
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


    @FXML
    private void textMiningButtonAction(ActionEvent e) {
        e.consume();

        /*
        try {
            URL sciGraphServerUrl = new URL("https://scigraph-ontology.monarchinitiative.org/scigraph/annotations/complete");
            Set<Main.PhenotypeTerm> phenotypeTerms = phenotypicFeaturesTableController.observablePhenotypeDescriptions().stream()
                    .map(toPhenotypeTerm())
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());

            HpoTextMining mining = HpoTextMining.builder()
                    .withSciGraphUrl(sciGraphServerUrl)
                    .withPhenotypeTerms(phenotypeTerms)
                    .withOntology(ontology.get()) // should not be null since the button is enabled only when the ontology is set
                    .build();

            Parent parent = mining.getMainParent();
            Stage stage = new Stage();
            stage.initOwner(ontologyTreeBrowser.getScene().getWindow());
            stage.setTitle("Mine phenotype features");
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.DECORATED);
            stage.showAndWait();

            Set<Main.PhenotypeTerm> approved = mining.getApprovedTerms();
            System.err.println("Approved:");
            approved.forEach(System.err::println);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    /*
    private Function<ObservablePhenotypicFeature, Optional<Main.PhenotypeTerm>> toPhenotypeTerm() {
        return opf -> {
            Ontology ontology = this.ontology.get();
            if (ontology == null)
                return Optional.empty();

            Term term = ontology.getTermMap().get(opf.getTermId());
            if (term == null)
                return Optional.empty();

            return Optional.of(new Main.PhenotypeTerm(term, !opf.isExcluded()));
        };
    }
     */
}
