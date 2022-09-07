package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.CommandLinksDialog;
import org.monarchinitiative.hpo_case_annotator.forms.FunctionalAnnotationRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.util.DialogUtil;
import org.monarchinitiative.hpo_case_annotator.forms.variants.input.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * This controller is the single source of truth regarding {@link ObservableCuratedVariant}s for
 * {@link org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy} or for
 * {@link org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy}.
 *
 * <h2>Properties</h2>
 * {@link VariantSummary} needs the following properties to be set in order to work:
 * <ul>
 *     <li>{@link #genomicAssemblyRegistryProperty()}</li>
 *     <li>{@link #functionalAnnotationRegistryProperty()}</li>
 * </ul>
 * <p>
 * {@link VariantSummary} exposes the following properties:
 * <ul>
 *     <li>{@link #variants()}</li>
 * </ul>
 */
public class VariantSummary extends VBox {

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantSummary.class);

    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>();
    private final ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistry = new SimpleObjectProperty<>();

    @FXML
    private VariantTable variantTable;
    @FXML
    private Button addVariantButton;
    @FXML
    private Button removeVariantButton;
    @FXML
    private Button editButton;

    public VariantSummary() {
        FXMLLoader loader = new FXMLLoader(VariantSummary.class.getResource("VariantSummary.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectProperty<ObservableList<ObservableCuratedVariant>> variants() {
        return variantTable.itemsProperty();
    }

    @FXML
    private void initialize() {
        variantTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // remove button is disabled when there are no variants in the table
        removeVariantButton.disableProperty().bind(variantTable.getSelectionModel().selectedItemProperty().isNull());
        editButton.disableProperty().bind(variantTable.getSelectionModel().selectedItemProperty().isNull());
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }

    public ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistryProperty() {
        return functionalAnnotationRegistry;
    }

    @FXML
    private void addVariantButtonAction() {
        askForVariantNotation()
                .flatMap(notation -> addEditVariant(notation, new ObservableCuratedVariant()))
                .ifPresent(newVariant -> variantTable.getItems().add(newVariant));
    }

    @FXML
    private void removeVariantButtonAction() {
        int selectedIndex = variantTable.getSelectionModel().getSelectedIndex();
        CuratedVariant v = variantTable.getItems().remove(selectedIndex);
//        LOGGER.info("Removed variant {}:{}:{}{}>{}", v.id(), v.getVariant().contigName(), v.getVariant().startWithCoordinateSystem(CoordinateSystem.oneBased()), v.getVariant().ref(), v.getVariant().alt());
    }

    @FXML
    private void editButtonAction() {
        int index = variantTable.getSelectionModel().getSelectedIndex();
        ObservableCuratedVariant variant = variantTable.getItems().get(index);

        Optional.ofNullable(variant.getVariantNotation())
                .or(this::askForVariantNotation) // The notation is missing from some reason
                .flatMap(notation -> addEditVariant(notation, variant));
    }

    private Optional<VariantNotation> askForVariantNotation() {
        var sequenceVariant = new CommandLinksDialog.CommandLinksButtonType("Sequence variant", "Both REF and ALT alleles are known.", true);
        var symbolicVariant = new CommandLinksDialog.CommandLinksButtonType("Symbolic variant", "The ALT allele is symbolic (E.g. \"<DEL>\").", false);
        var breakendVariant = new CommandLinksDialog.CommandLinksButtonType("Breakend variant", "A rearrangement involving two chromosomes.", false);
        CommandLinksDialog dialog = new CommandLinksDialog(sequenceVariant, symbolicVariant, breakendVariant);
        dialog.setTitle("New variant");
        dialog.setHeaderText("Select variant notation");
        dialog.setContentText("The following variant notations are supported:");

        return dialog.showAndWait()
                .flatMap(bt -> {
                    if (bt.equals(sequenceVariant.getButtonType())) {
                        return Optional.of(VariantNotation.SEQUENCE);
                    } else if (bt.equals(symbolicVariant.getButtonType())) {
                        return Optional.of(VariantNotation.SYMBOLIC);
                    } else if (bt.equals(breakendVariant.getButtonType())) {
                        return Optional.of(VariantNotation.BREAKEND);
                    } else {
                        return Optional.empty();
                    }
                });
    }

    /**
     * Presents a {@link org.monarchinitiative.hpo_case_annotator.forms.DataEdit} for given {@code variant} and
     * {@code notation}. If the user chooses to commit the changes, the updated variant is returned inside
     * the {@link Optional}. If the user chooses to cancel without committing, the {@link Optional#empty()} is returned.
     */
    private Optional<ObservableCuratedVariant> addEditVariant(VariantNotation notation,
                                                              ObservableCuratedVariant variant) {
        // (*) Setup content
        BaseVariantDataEdit content = switch (notation) {
            case SEQUENCE -> new VcfSequenceVariantDataEdit();
            case SYMBOLIC -> new VcfSymbolicVariantDataEdit();
            case BREAKEND -> new VcfBreakendVariantDataEdit();
        };

        // bind properties
        content.genomicAssemblyRegistryProperty().bind(genomicAssemblyRegistry);
        content.functionalAnnotationRegistryProperty().bind(functionalAnnotationRegistry);
        content.setInitialData(variant); // TODO - check non null?

        // (*) Setup dialog
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(variantTable.getParent().getScene().getWindow());
        dialog.initStyle(StageStyle.DECORATED);
        dialog.setTitle("Edit variant"); // TODO - set proper title
        dialog.setResizable(true);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(DialogUtil.OK_CANCEL_BUTTONS);
        dialog.setResultConverter(bt -> bt.getButtonData().equals(ButtonBar.ButtonData.OK_DONE));

        return dialog.showAndWait()
                .flatMap(shouldCommit -> {
                    if (shouldCommit) {
                        content.commit();
                        return Optional.of(variant);
                    } else
                        return Optional.empty();
                });
    }

//    /**
//     * Open edit dialog after double-clicking on a variant.
//     *
//     * @param e mouse event
//     */
//    @FXML
//    private void variantTableViewOnMouseClicked(MouseEvent e) {
//        if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
//            int index = variantTable.getSelectionModel().getSelectedIndex();
//            ObservableCuratedVariant selectedItem = variantTable.getItems().get(index);
//            Optional.ofNullable(selectedItem.getVariantNotation())
//                    .flatMap(notation -> addEditVariant(notation, selectedItem))
//                    .ifPresent(edited -> variantTable.getItems().set(index, edited));
//
//            e.consume();
//        }
//    }

}
