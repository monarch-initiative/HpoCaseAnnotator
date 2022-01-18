package org.monarchinitiative.hpo_case_annotator.forms.v2.individual;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IndividualVariantSummaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualVariantSummaryController.class);

    private final ObservableList<CuratedVariant> variants = FXCollections.observableList(new LinkedList<>());
    private final ObservableMap<String, Genotype> genotypes = FXCollections.observableHashMap();

    @FXML
    private TableView<GenotypedVariant> variantTableView;

    @FXML
    private TableColumn<GenotypedVariant, Genotype> genotypeTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> variantIdTableColumn;
    @FXML
    private TableColumn<GenotypedVariant, String> genomicAssemblyTableColumn;
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
        genotypeTableColumn.setOnEditCommit(commitGenotypeChange());

        variantIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().genomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().contig().name()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().curatedVariant().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().curatedVariant().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().curatedVariant().alt()));

        variants.addListener(createCuratedVariantChangeListener());
    }

    private EventHandler<TableColumn.CellEditEvent<GenotypedVariant, Genotype>> commitGenotypeChange() {
        return e -> {
            GenotypedVariant variant = e.getRowValue();
            variant.setGenotype(e.getNewValue());
            genotypes.put(variant.curatedVariant().md5Hex(), e.getNewValue());
        };
    }

    private ListChangeListener<CuratedVariant> createCuratedVariantChangeListener() {
        return c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (CuratedVariant addedVariant : c.getAddedSubList()) {
                        Genotype genotype = genotypes.getOrDefault(addedVariant.md5Hex(), Genotype.UNKNOWN);
                        variantTableView.getItems().add(GenotypedVariant.of(addedVariant, genotype));
                    }
                } else if (c.wasRemoved()) {
                    List<Integer> indices = new ArrayList<>(2);
                    for (CuratedVariant variantToRemove : c.getRemoved()) {
                        // find indices of the variantToRemove
                        int idx = 0;
                        for (GenotypedVariant gv : variantTableView.getItems()) {
                            if (gv.curatedVariant().md5Hex().equals(variantToRemove.md5Hex()))
                                indices.add(idx);
                            idx++;
                        }

                        // remove the variants at present indices (in case the same variant is entered multiple-times, although that should not happen)
                        for (Integer index : indices) {
                            variantTableView.getItems().remove((int) index);
                        }

                        indices.clear();

                        // remove the variant from the genotype map
                        genotypes.remove(variantToRemove.md5Hex());
                    }
                } else //noinspection StatementWithEmptyBody
                    if (c.wasReplaced()) {
                        // We do not handle `c.wasReplaced()` since we handle `c.wasAdded()` and `c.wasRemoved()` (see Javadoc for `c.wasReplaced()`)
                    } else {
                        LOGGER.warn("Unexpected list change operation was performed: {}", c);
                    }
            }
        };
    }

    public ObservableMap<String, Genotype> genotypes() {
        return genotypes;
    }

    public ObservableList<CuratedVariant> variants() {
        return variants;
    }
}