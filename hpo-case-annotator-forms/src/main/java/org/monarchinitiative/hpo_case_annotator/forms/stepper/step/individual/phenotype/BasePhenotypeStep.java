package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.phenotype;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BaseAddClinicalEncounter;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.BrowseHpo;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypeTable;
import org.monarchinitiative.hpo_case_annotator.forms.phenotype.PhenotypicFeatureBinding;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.stream.Stream;

public abstract class BasePhenotypeStep<T extends BaseObservableIndividual> extends BaseStep<T> {
    // TODO - this is very similar to PhenotypeDataEdit. Perhaps we can extract the shared functionality?

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    @FXML
    private Button browseHpo;
    @FXML
    private Button addClinicalEncounter;
    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private PhenotypicFeatureBinding phenotypicFeature;

    public BasePhenotypeStep() {
        super(BasePhenotypeStep.class.getResource("PhenotypeStep.fxml"));
        phenotypicFeature.hpoProperty().bind(hpo);
    }

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    @Override
    protected void initialize() {
        super.initialize();
        phenotypicFeature.dataProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty());
        phenotypicFeature.disableProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(); // no-op
    }

    @Override
    public void invalidated(Observable observable) {
        // no-op
    }

    @Override
    protected void bind(T data) {
        if (data != null)
            phenotypeTable.phenotypicFeaturesProperty().bindBidirectional(data.phenotypicFeaturesProperty());
        else
            phenotypeTable.getPhenotypicFeatures().clear();
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            phenotypeTable.phenotypicFeaturesProperty().unbindBidirectional(data.phenotypicFeaturesProperty());
    }

    @FXML
    private void removeButtonAction(ActionEvent e) {
        int selectedIndex = phenotypeTable.getSelectionModel().getSelectedIndex();
        // The remove button is disabled if pedigree is null, hence the pedigree should not be null here!
        // Contents of membersProperty() should not be null since `Pedigree` initializes the value with an empty list.
        phenotypeTable.getPhenotypicFeatures().remove(selectedIndex);

        e.consume();
    }
    @FXML
    private void browseHpoAction(ActionEvent e) {
        BrowseHpo component = new BrowseHpo();
        component.hpoProperty().bind(hpo);
        component.setPhenotypicFeatureConsumer(phenotypeTable.getPhenotypicFeatures()::add);

        showComponentNodeDialog(component);

        e.consume();
    }

    @FXML
    private void addClinicalEncounterAction(ActionEvent e) {
        BaseAddClinicalEncounter<T> component = clinicalEncounterComponent();
        component.hpoProperty().bind(hpo);
        component.setData(data.get()); // TODO - should not be null but maybe..

        showComponentNodeDialog(component);
        phenotypeTable.getPhenotypicFeatures().addAll(component.itemsProperty().get());

        e.consume();
    }

    protected abstract BaseAddClinicalEncounter<T> clinicalEncounterComponent();

    private static void showComponentNodeDialog(Node component) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }

}
