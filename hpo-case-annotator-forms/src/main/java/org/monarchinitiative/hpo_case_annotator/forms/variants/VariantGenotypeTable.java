package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.GenotypeStringConverter;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableVariantGenotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class VariantGenotypeTable extends VBoxObservableDataController<BaseObservableIndividual> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantGenotypeTable.class);

    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private TableView<ObservableCuratedVariant> genotypesTable;
    @FXML
    private TableColumn<ObservableCuratedVariant, Genotype> genotypeTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> variantIdTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> genomicAssemblyTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> contigTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> startTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> endTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> refTableColumn;
    @FXML
    private TableColumn<ObservableCuratedVariant, String> altTableColumn;

    public VariantGenotypeTable() {
        FXMLLoader loader = new FXMLLoader(VariantGenotypeTable.class.getResource("VariantGenotypeTable.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    @Override
    protected void initialize() {
        genotypeTableColumn.setCellValueFactory(cdf -> extractGenotype(cdf.getValue().md5Hex(), data));
        genotypeTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(GenotypeStringConverter.getInstance(), Genotype.values()));

        variantIdTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getId()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getGenomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(Utils.getContigNameOrEmptyString(cdf.getValue().getContig())));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(Formats.NUMBER_FORMAT.format(cdf.getValue().getStart())));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(Formats.NUMBER_FORMAT.format(cdf.getValue().getEnd())));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getRef()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getAlt()));

        variants.addListener(handleAddingAndRemovingVariant(data));
        genotypesTable.itemsProperty().bind(variants);
    }

    private static <T extends BaseObservableIndividual> ObservableValue<Genotype> extractGenotype(String variantMd5Hex, ObjectProperty<T> member) {
        T m = member.get();
        if (m == null)
            return null;

        return m.getGenotypes().stream()
                .filter(g -> g.getMd5Hex().equals(variantMd5Hex))
                .findFirst()
                .map(ObservableVariantGenotype::genotypeProperty)
                .orElse(null);
    }

    private static <T extends BaseObservableIndividual> ListChangeListener<? super CuratedVariant> handleAddingAndRemovingVariant(ObjectProperty<T> item) {
        return change -> {
            T member = item.get();
            if (member == null)
                // Nothing to do
                return;

            while (change.next()) {
                if (change.wasAdded()) {
                    for (CuratedVariant cv : change.getAddedSubList()) {
                        // Add `ObservableVariantGenotype` to the individual if not already present.
                        if (member.getGenotypes().stream().noneMatch(ovg -> ovg.getMd5Hex().equals(cv.md5Hex()))) {
                            ObservableVariantGenotype vg = new ObservableVariantGenotype();
                            vg.setMd5Hex(cv.md5Hex());
                            vg.setGenotype(Genotype.UNSET);
                            member.getGenotypes().add(vg);
                        }
                    }
                } else if (change.wasRemoved()) {
                    for (CuratedVariant cv : change.getRemoved()) {
                        Optional<ObservableVariantGenotype> opt = member.getGenotypes().stream()
                                .filter(ovg -> ovg.getMd5Hex().equals(cv.md5Hex()))
                                .findFirst();
                        if (opt.isPresent())
                            member.getGenotypes().remove(opt.get());
                        else
                            LOGGER.warn("Removed variant without genotype: {}", cv.md5Hex());
                    }
                } else {
                    LOGGER.info("Variant change: {}", change);
                }
            }
        };
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

}
