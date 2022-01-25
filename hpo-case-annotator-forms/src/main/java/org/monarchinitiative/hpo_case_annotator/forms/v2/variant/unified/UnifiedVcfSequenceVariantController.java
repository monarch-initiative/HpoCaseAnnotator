package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.obsoleted.DeprecatedGenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UnifiedVcfSequenceVariantController extends VcfSeqSymVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedVcfSequenceVariantController.class);

    @FXML
    private TextField positionTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private TextField alternativeTextField;

    private ObjectBinding<Integer> startBinding;
    private ObjectBinding<Integer> endBinding;

    public UnifiedVcfSequenceVariantController(DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        positionTextField.setTextFormatter(Formats.numberFormatter());

        startBinding = Bindings.createObjectBinding(() -> processFormattedInteger(positionTextField.getText()).orElse(null), positionTextField.textProperty());
        endBinding = Bindings.createObjectBinding(() -> {
                    Optional<Integer> startOpt = processFormattedInteger(positionTextField.getText());
                    if (startOpt.isEmpty()) return null;
                    Integer start = startOpt.get();
                    return start + referenceTextField.getText().length() - 1;
                },
                positionTextField.textProperty(), referenceTextField.textProperty());
    }

    protected void unbind(ObservableSeqSymVariant variant) {
        super.unbind(variant);

        variant.startProperty().unbind();
        variant.endProperty().unbind();
        referenceTextField.textProperty().unbindBidirectional(variant.refProperty());
        alternativeTextField.textProperty().unbindBidirectional(variant.altProperty());
    }

    protected void bind(ObservableSeqSymVariant variant) {
        super.bind(variant);
        positionTextField.setText(String.valueOf(variant.getStart()));
        variant.startProperty().bind(startBinding);
        variant.endProperty().bind(endBinding);

        referenceTextField.textProperty().bindBidirectional(variant.refProperty());
        alternativeTextField.textProperty().bindBidirectional(variant.altProperty());
    }

    @Override
    protected ChangeListener<ObservableSeqSymVariant> variantChangeListener() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

//    @Override
//    public CuratedVariant getComponent() throws InvalidComponentDataException {
//        GenomicAssemblyService assemblyService = this.assemblyService.get();
//        if (assemblyService == null) {
//            throw new InvalidComponentDataException("Genomic assembly is not selected");
//        }
//
//        Contig contig = contigComboBox.getValue();
//        int pos = processFormattedInteger(positionTextField.getText());
//        // TODO - add ID
//        Variant variant = Variant.of(contig, "abc", VCF_STRAND, VCF_COORDINATE_SYSTEM, pos, referenceTextField.getText(), alternativeTextField.getText());
//        StructuralVariantMetadata metadata = StructuralVariantMetadata.of("", "", "", false, false);
//
//        return CuratedVariant.of(variant, metadata);
//    }
}
