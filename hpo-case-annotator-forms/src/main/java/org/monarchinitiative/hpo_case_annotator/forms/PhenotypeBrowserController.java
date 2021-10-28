package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.phenol.ontology.data.Ontology;

// should be singleton
public class PhenotypeBrowserController {

    // TODO - this is an intermediate solution. In real life, ontology should be bound to sub-controllers within the controller factory
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

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    @FXML
    private void initialize() {
        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
        ontologyTreeBrowserController.phenotypeDescriptionInFocusProperty().bind(phenotypicFeaturesTableController.phenotypeDescriptionInFocusProperty());
        phenotypicFeatureController.phenotypeDescriptionProperty().bind(phenotypicFeaturesTableController.phenotypeDescriptionInFocusProperty());
        phenotypicFeaturesTableController.phenotypeDescriptions().addAll(TestData.sampleValues());
    }

    public ObservableList<PhenotypeDescription> phenotypeDescriptions() {
        return phenotypicFeaturesTableController.phenotypeDescriptions();
    }

}
