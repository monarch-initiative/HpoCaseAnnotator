package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;

import java.io.IOException;

public class VariantTable extends VBox {

    @FXML
    private TableView<CuratedVariant> variantTable;
    @FXML
    private TableColumn<CuratedVariant, String> idTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> genomicAssemblyTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> contigTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> startTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> endTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> refTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> altTableColumn;

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
        variantTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        idTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getGenomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().contigName()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().alt()));
    }


    public ObservableList<CuratedVariant> getItems() {
        return variantTable.getItems();
    }

    public ObjectProperty<ObservableList<CuratedVariant>> itemsProperty() {
        return variantTable.itemsProperty();
    }

    public TableView.TableViewSelectionModel<CuratedVariant> getSelectionModel() {
        return variantTable.getSelectionModel();
    }
}
