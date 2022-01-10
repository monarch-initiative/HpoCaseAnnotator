package org.monarchinitiative.hpo_case_annotator.forms.variant.unified;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.svart.Contig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The controller manages contig for sequence and symbolic variants.
 */
public abstract class VcfSeqSymVariantController extends UnifiedBaseVariantController<ObservableSeqSymVariant> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcfSeqSymVariantController.class);

    @FXML
    protected ComboBox<Contig> contigComboBox;

    // TODO - add variant ID, start and end

    @FXML
    protected void initialize() {
        super.initialize();
        FormUtils.initializeContigComboBox(contigComboBox, assemblyService);
    }

    protected VcfSeqSymVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    protected void unbind(ObservableSeqSymVariant variant) {
        super.unbind(variant);
        contigComboBox.valueProperty().unbindBidirectional(variant.contigProperty());
    }

    protected void bind(ObservableSeqSymVariant variant) {
        super.bind(variant);
        contigComboBox.valueProperty().bindBidirectional(variant.contigProperty());
    }

    protected static Optional<Integer> processFormattedInteger(String integerText) {
        try {
            return Optional.of(Integer.parseInt(integerText.replaceAll(",", "")));
        } catch (NumberFormatException e) {
            LOGGER.debug("Invalid number `{}`", integerText);
            return Optional.empty();
        }
    }
}
