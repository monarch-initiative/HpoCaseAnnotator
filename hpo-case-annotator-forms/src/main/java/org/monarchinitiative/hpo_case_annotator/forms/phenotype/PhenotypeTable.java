package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

import java.io.IOException;

public class PhenotypeTable extends VBox {

    @FXML
    private StackPane contentPane;
    @FXML
    private ListView<ObservablePhenotypicFeature> phenotypes;
    @FXML
    private Button removePhenotypicFeature;
//    @FXML
//    private TableColumn<ObservablePhenotypicFeature, String> modifiersColumn;


    public PhenotypeTable() {
        FXMLLoader loader = new FXMLLoader(PhenotypeTable.class.getResource("PhenotypeTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize() {
        phenotypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Phenotypes table view
        phenotypes.setCellFactory(lv -> new PhenotypeListCell());
        removePhenotypicFeature.visibleProperty().bind(contentPane.hoverProperty());
        removePhenotypicFeature.disableProperty().bind(phenotypes.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void removePhenotypicFeatureAction(ActionEvent e) {
        int idx = phenotypes.getSelectionModel().getSelectedIndex();
        phenotypes.getItems().remove(idx);
        e.consume();
    }

    public ObservableList<ObservablePhenotypicFeature> getPhenotypicFeatures() {
        return phenotypes.getItems();
    }

    public ObjectProperty<ObservableList<ObservablePhenotypicFeature>> phenotypicFeaturesProperty() {
        return phenotypes.itemsProperty();
    }

    public MultipleSelectionModel<ObservablePhenotypicFeature> getSelectionModel() {
        return phenotypes.getSelectionModel();
    }
}
