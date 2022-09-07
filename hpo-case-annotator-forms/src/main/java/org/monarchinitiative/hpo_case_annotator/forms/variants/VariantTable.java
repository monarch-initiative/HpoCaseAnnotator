package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;

import java.io.IOException;

public class VariantTable extends VBox {

    @FXML
    private ListView<ObservableCuratedVariant> variantList;

    public VariantTable() {
        FXMLLoader loader = new FXMLLoader(VariantTable.class.getResource("VariantTable.fxml"));
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
        variantList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        variantList.setCellFactory(lv -> new ObservableCuratedVariantListCell());
    }


    public ObservableList<ObservableCuratedVariant> getItems() {
        return variantList.getItems();
    }

    public ObjectProperty<ObservableList<ObservableCuratedVariant>> itemsProperty() {
        return variantList.itemsProperty();
    }

    public MultipleSelectionModel<ObservableCuratedVariant> getSelectionModel() {
        return variantList.getSelectionModel();
    }
}
