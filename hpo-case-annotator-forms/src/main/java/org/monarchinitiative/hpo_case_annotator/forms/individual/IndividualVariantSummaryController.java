package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;

public class IndividualVariantSummaryController {

    @FXML
    private TableView<GenotypedVariant> variantTableView;
    @FXML
    private TableColumn<GenotypedVariant, String> variantIdTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> genomicAssemblyTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, Genotype> genotypeTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> contigTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> startTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> endTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> refTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> altTableColumn;

    @FXML
    private void initialize() {
        variantTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        genotypeTableColumn.setCellValueFactory(cdf -> cdf.getValue().genotypeProperty());
        genotypeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Genotype.values()));
        variantIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().genomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().contig().name()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().curatedVariant().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().curatedVariant().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().alt()));
    }

    public ObservableList<GenotypedVariant> genotypedVariants() {
        return variantTableView.getItems();
    }

}
