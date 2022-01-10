package org.monarchinitiative.hpo_case_annotator.forms;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.model.PhenotypeDescription;
import org.monarchinitiative.hpo_case_annotator.forms.ontotree.OntologyTreeBrowserController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeEntryController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureController;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeaturesTableController;
import org.monarchinitiative.phenol.ontology.data.Ontology;


// should be singleton
@Deprecated
// TODO - This was the 1st uber controller, now the idea is to have one controller for term review and the 2nd for term entry.
// The review controller is `PhenotypeBrowserController` and the term entry controller is this one.
public class PhenotypeController {

    // TODO - this is an intermediate solution. In real life, ontology should be bound to sub-controllers within the controller factory
    private final ObjectProperty<Ontology> ontology = new SimpleObjectProperty<>(this, "ontology");
    @FXML
    private VBox ontologyTreeBrowser;
    @FXML
    private OntologyTreeBrowserController ontologyTreeBrowserController;
    @FXML
    private TabPane phenotypeEntry;
    @FXML
    private PhenotypeEntryController phenotypeEntryController;
    @FXML
    private VBox phenotypicFeature;
    @FXML
    private PhenotypicFeatureController phenotypicFeatureController;
    @FXML
    private VBox phenotypicFeaturesTable;
    @FXML
    private PhenotypicFeaturesTableController phenotypicFeaturesTableController;

    public ObjectProperty<Ontology> ontologyProperty() {
        return ontology;
    }

    public ObservableList<PhenotypeDescription> phenotypeDescriptions() {
        return phenotypicFeaturesTableController.phenotypeDescriptions();
    }

    @FXML
    private void initialize() {
        phenotypicFeatureController.phenotypeDescriptionProperty().bind(phenotypicFeaturesTableController.phenotypeDescriptionInFocusProperty());
        ontologyTreeBrowserController.phenotypeDescriptionInFocusProperty().bind(phenotypicFeaturesTableController.phenotypeDescriptionInFocusProperty());
        ontologyTreeBrowserController.ontologyProperty().bind(ontology);
    }

}
