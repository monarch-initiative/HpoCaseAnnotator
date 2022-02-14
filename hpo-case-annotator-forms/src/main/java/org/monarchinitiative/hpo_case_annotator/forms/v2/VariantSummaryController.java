package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VariantNotation;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfBreakendVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSequenceVariantController;
import org.monarchinitiative.hpo_case_annotator.forms.v2.variant.VcfSymbolicVariantController;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.CoordinateSystem;
import org.monarchinitiative.svart.Strand;
import org.monarchinitiative.svart.GenomicVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * This controller is the single source of truth regarding {@link CuratedVariant}s for
 * {@link org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy} or for
 * {@link org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy}.
 */
public class VariantSummaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantSummaryController.class);

    private final HCAControllerFactory controllerFactory;

    @FXML
    private TableView<CuratedVariant> variantTableView;
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
    @FXML
    private Button addVariantButton;
    @FXML
    private Button removeVariantButton;
    @FXML
    private Button editButton;

    public VariantSummaryController(HCAControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        variantTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        idTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().id()));
        genomicAssemblyTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().genomicAssembly()));
        contigTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().contig().name()));
        startTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().startOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        endTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(Formats.NUMBER_FORMAT.format(cdf.getValue().endOnStrandWithCoordinateSystem(Strand.POSITIVE, CoordinateSystem.oneBased()))));
        refTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().ref()));
        altTableColumn.setCellValueFactory(cdf -> new ReadOnlyObjectWrapper<>(cdf.getValue().alt()));

        // remove button is disabled when there are no variants in the table
        removeVariantButton.disableProperty().bind(variantTableView.getSelectionModel().selectedItemProperty().isNull());
        editButton.disableProperty().bind(variantTableView.getSelectionModel().selectedItemProperty().isNull());

    }

    @FXML
    private void addVariantButtonAction() {
        var sequenceVariant = new CommandLinksDialog.CommandLinksButtonType("Sequence variant", "Both REF and ALT alleles are known.", true);
        var symbolicVariant = new CommandLinksDialog.CommandLinksButtonType("Symbolic variant", "The ALT allele is symbolic (E.g. \"<DEL>\").", false);
        var breakendVariant = new CommandLinksDialog.CommandLinksButtonType("Breakend variant", "A rearrangement involving two chromosomes.", false);
        CommandLinksDialog dialog = new CommandLinksDialog(sequenceVariant, symbolicVariant, breakendVariant);
        dialog.setTitle("New variant");
        dialog.setHeaderText("Select variant notation");
        dialog.setContentText("The following variant notations are supported:");

        dialog.showAndWait().flatMap(bt -> {
                    if (bt.equals(sequenceVariant.getButtonType())) {
                        return Optional.of(VariantNotation.SEQUENCE);
                    } else if (bt.equals(symbolicVariant.getButtonType())) {
                        return Optional.of(VariantNotation.SYMBOLIC);
                    } else if (bt.equals(breakendVariant.getButtonType())) {
                        return Optional.of(VariantNotation.BREAKEND);
                    } else {
                        return Optional.empty();
                    }
                }).flatMap(notation -> addEditVariant(notation, null))
                .ifPresent(variantTableView.getItems()::add);
    }

    private Optional<CuratedVariant> addEditVariant(VariantNotation notation, CuratedVariant variant) {
        URL controllerFxmlUrl = switch (notation) {
            case SEQUENCE -> VcfSequenceVariantController.class.getResource("VcfSequenceVariant.fxml");
            case SYMBOLIC -> VcfSymbolicVariantController.class.getResource("VcfSymbolicVariant.fxml");
            case BREAKEND -> VcfBreakendVariantController.class.getResource("VcfBreakendVariant.fxml");
        };

        try {
            // setup controller
            FXMLLoader loader = new FXMLLoader(controllerFxmlUrl);
            loader.setControllerFactory(controllerFactory);
            Parent parent = loader.load();
            ComponentController<CuratedVariant> controller = loader.getController();
            if (variant != null)
                controller.presentComponent(variant);

            // setup stage
            Stage stage = new Stage();
            stage.setTitle(notation.label());
            stage.initOwner(variantTableView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();

            return Optional.ofNullable(controller.getComponent());
        } catch (IOException | InvalidComponentDataException e) {
            LOGGER.warn("Unable to add variant: {}", e.getMessage());
            LOGGER.debug("Unable to add variant: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @FXML
    private void removeVariantButtonAction() {
        int selectedIndex = variantTableView.getSelectionModel().getSelectedIndex();
        CuratedVariant v = variantTableView.getItems().remove(selectedIndex);
        LOGGER.info("Removed variant {}:{}:{}{}>{}", v.id(), v.contig().name(), v.startWithCoordinateSystem(CoordinateSystem.oneBased()), v.ref(), v.alt());
    }

    @FXML
    private void editButtonAction() {
        int index = variantTableView.getSelectionModel().getSelectedIndex();
        CuratedVariant variant = variantTableView.getItems().get(index);
        VariantNotation notation = resolveVariantNotation(variant.variant());
        addEditVariant(notation, variant)
                .ifPresent(edited -> variantTableView.getItems().set(index, edited));
    }

    private static VariantNotation resolveVariantNotation(GenomicVariant variant) {
        if (variant.isBreakend()) {
            return VariantNotation.BREAKEND;
        } else if (variant.isSymbolic()) {
            return VariantNotation.SYMBOLIC;
        } else {
            return VariantNotation.SEQUENCE;
        }
    }

    public ObservableList<CuratedVariant> curatedVariants() {
        return variantTableView.getItems();
    }

    /**
     * Open edit dialog after double-clicking on a variant.
     *
     * @param e mouse event
     */
    @FXML
    private void variantTableViewOnMouseClicked(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
            int index = variantTableView.getSelectionModel().getSelectedIndex();
            CuratedVariant selectedItem = variantTableView.getItems().get(index);
            addEditVariant(resolveVariantNotation(selectedItem.variant()), selectedItem)
                    .ifPresent(edited -> variantTableView.getItems().set(index, edited));

            e.consume();
        }
    }

}
