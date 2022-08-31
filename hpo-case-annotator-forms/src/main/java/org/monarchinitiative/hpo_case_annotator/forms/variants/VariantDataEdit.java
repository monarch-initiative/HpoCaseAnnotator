package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.DataEditController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.GenotypeStringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVariantGenotype;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class VariantDataEdit<T extends BaseObservableIndividual> extends VBox implements DataEditController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantDataEdit.class);

    private final ListProperty<CuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    private BaseObservableIndividual item;

    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;

    @FXML
    private TableView<CuratedVariant> genotypesTable;
    @FXML
    private TableColumn<CuratedVariant, Genotype> genotypeTableColumn;
    @FXML
    private TableColumn<CuratedVariant, String> variantIdTableColumn;
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

    @FXML
    private void initialize() {
        // Genotypes table view
        genotypeTableColumn.setCellValueFactory(cdf -> extractGenotype(cdf.getValue().md5Hex(), item));
        genotypeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(GenotypeStringConverter.getInstance(), Genotype.values()));

        variantIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getGenomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().contigName()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().getVariant().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().getVariant().alt()));
    }

    @Override
    public void setInitialData(BaseObservableIndividual data) {
        item = Objects.requireNonNull(data);

        individualIds.setData(data);

        variants.addListener(handleAddingAndRemovingVariant(item));
        genotypesTable.itemsProperty().bind(variants);
    }

    @Override
    public void commit() {
        // TODO - implement if possible and practical
    }

    public ListProperty<CuratedVariant> variantsProperty() {
        return variants;
    }

    private static ListChangeListener<? super CuratedVariant> handleAddingAndRemovingVariant(BaseObservableIndividual item) {
        return change -> {
            if (item == null)
                // Nothing to do
                return;

            while (change.next()) {
                if (change.wasAdded()) {
                    for (CuratedVariant cv : change.getAddedSubList()) {
                        if (item.getGenotypes().stream().noneMatch(ovg -> ovg.getMd5Hex().equals(cv.md5Hex()))) {
                            ObservableVariantGenotype vg = new ObservableVariantGenotype();
                            vg.setMd5Hex(cv.md5Hex());
                            vg.setGenotype(Genotype.UNSET);
                            item.getGenotypes().add(vg);
                        }
                    }
                } else if (change.wasRemoved()) {
                    LOGGER.warn("Unexpected variant removal.");
                } else {
                    LOGGER.info("Variant change: {}", change);
                }
            }
        };
    }

    private static ObservableValue<Genotype> extractGenotype(String variantMd5Hex, BaseObservableIndividual member) {
        if (member == null)
            return null;

        return member.getGenotypes().stream()
                .filter(g -> g.getMd5Hex().equals(variantMd5Hex))
                .findFirst()
                .map(ObservableVariantGenotype::genotypeProperty)
                .orElse(null);
    }

}
