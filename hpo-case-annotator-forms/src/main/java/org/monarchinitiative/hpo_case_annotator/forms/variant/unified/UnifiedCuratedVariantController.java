package org.monarchinitiative.hpo_case_annotator.forms.variant.unified;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.ComponentController;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.StatusBarController;
import org.monarchinitiative.hpo_case_annotator.forms.variant.cache.HgvsVariantController;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

public class UnifiedCuratedVariantController implements ComponentController<CuratedVariant> {

    private final ObjectProperty<ObservableSeqSymVariant> observableSeqSymVariant = new SimpleObjectProperty<>(this, "seqSymVariant");
    private final ObjectProperty<ObservableBreakendVariant> observableBreakendVariant = new SimpleObjectProperty<>(this, "breakendVariant");

    @FXML
    private Tab sequenceVariantTab;
    @FXML
    private VBox sequenceVariant;
    @FXML
    private UnifiedVcfSequenceVariantController sequenceVariantController;
    @FXML
    private Tab symbolicVariantTab;
    @FXML
    private VBox symbolicVariant;
    @FXML
    private UnifiedVcfSymbolicVariantController symbolicVariantController;
    @FXML
    private Tab breakendVariantTab;
    @FXML
    private VBox breakendVariant;
    @FXML
    private UnifiedVcfBreakendVariantController breakendVariantController;
    @FXML
    private Tab hgvsVariantTab;
    @FXML
    private VBox hgvsVariant;
    @FXML
    private HgvsVariantController hgvsVariantController;
    @FXML
    private CheckBox breakendCheckBox;

    @FXML
    private HBox statusBar;
    @FXML
    private StatusBarController statusBarController;

    @FXML
    private void initialize() {
        sequenceVariantTab.disableProperty().bind(breakendCheckBox.selectedProperty());
        symbolicVariantTab.disableProperty().bind(breakendCheckBox.selectedProperty());
        breakendVariantTab.disableProperty().bind(breakendCheckBox.selectedProperty().not());
        hgvsVariantTab.disableProperty().bind(breakendCheckBox.selectedProperty());

        observableSeqSymVariant.addListener(sequenceVariantController.variantChangeListener());
        observableSeqSymVariant.addListener(symbolicVariantController.variantChangeListener());
        observableBreakendVariant.addListener(breakendVariantController.variantChangeListener());
        observableSeqSymVariant.addListener(hgvsVariantController.variantChangeListener());
    }


    @Override
    public void presentComponent(CuratedVariant variant) {
        Convert.toObservableSeqSymVariant(variant)
                .ifPresent(observableSeqSymVariant::set);
        Convert.toObservableBreakendVariant(variant)
                .ifPresent(observableBreakendVariant::set);
    }

    @Override
    public CuratedVariant getComponent() throws InvalidComponentDataException {
        if (observableSeqSymVariant.get() != null) {
            return Convert.toCuratedVariant(observableSeqSymVariant.get());
        } else if (observableBreakendVariant.get() != null) {
            return Convert.toCuratedVariant(observableBreakendVariant.get());
        } else {
            throw new IllegalStateException("Either sequence/symbolic variant or breakend variant must be set!");
        }
    }

}
