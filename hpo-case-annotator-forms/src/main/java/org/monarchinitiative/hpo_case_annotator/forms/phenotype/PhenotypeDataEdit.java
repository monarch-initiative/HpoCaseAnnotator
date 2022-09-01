package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.Objects;

public abstract class PhenotypeDataEdit<T extends BaseObservableIndividual> extends VBox implements DataEditController<T> {

    private final ObjectProperty<Ontology> hpo = new SimpleObjectProperty<>();
    private BaseObservableIndividual item;
    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;
    @FXML
    private Button browseHpo;
    @FXML
    private Button addClinicalEncounter;
    @FXML
    private PhenotypeTable phenotypeTable;
    @FXML
    private PhenotypicFeatureBinding phenotypicFeature;

    public ObjectProperty<Ontology> hpoProperty() {
        return hpo;
    }

    @FXML
    private void initialize() {
        phenotypicFeature.hpoProperty().bind(hpo);
        phenotypicFeature.dataProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty());
        phenotypicFeature.disableProperty().bind(phenotypeTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void setInitialData(BaseObservableIndividual data) {
        item = Objects.requireNonNull(data);

        individualIds.setData(data);

        phenotypeTable.getItems().addAll(item.getPhenotypicFeatures());
    }

    @FXML
    private void browseHpoAction(ActionEvent e) {
        BrowseHpo component = new BrowseHpo();
        component.hpoProperty().bind(hpo);
        component.setPhenotypicFeatureConsumer(phenotypeTable.getItems()::add);

        showComponentNodeDialog(component);

        e.consume();
    }

    @FXML
    private void addClinicalEncounterAction(ActionEvent e) {
        AddClinicalEncounter component = new AddClinicalEncounter();
        component.hpoProperty().bind(hpo);
        component.setData(item);

        showComponentNodeDialog(component);
        // TODO - allow cancelling the dialog, resulting in not adding the terms.
        phenotypeTable.getItems().addAll(component.itemsProperty().get());

        e.consume();
    }

    private static void showComponentNodeDialog(Node component) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(component);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }

    @Override
    public void commit() {
        item.setPhenotypicFeatures(phenotypeTable.getItems());
    }
}
