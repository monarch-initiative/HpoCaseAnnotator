package org.monarchinitiative.hpo_case_annotator.forms.phenotype;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservablePhenotypicFeature;

import java.io.IOException;

/**
 * A thin wrapper around a {@link ListView} that manages {@link ObservablePhenotypicFeature}s.
 */
public class PhenotypeTable extends VBox {

    @FXML
    private ListView<ObservablePhenotypicFeature> phenotypes;

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
        phenotypes.setCellFactory(lv -> new PhenotypeListCell());
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
