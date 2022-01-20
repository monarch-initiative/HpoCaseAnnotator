package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.BreakendVariant;
import org.monarchinitiative.svart.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcfBreakendVariantController extends BaseVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcfBreakendVariantController.class);

    @FXML
    private TextField eventIdTextField;
    @FXML
    private VBox leftBreakend;
    @FXML
    private BreakendController leftBreakendController;
    @FXML
    private VBox rightBreakend;
    @FXML
    private BreakendController rightBreakendController;
    @FXML
    private TextField referenceSequenceTextField;

    @FXML
    private TextField insertedSequenceTextField;

    public VcfBreakendVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        leftBreakendController.assemblyServiceProperty().bind(assemblyService);
        rightBreakendController.assemblyServiceProperty().bind(assemblyService);

    }

    @Override
    public void presentComponent(CuratedVariant variant) {
        if (variant.variant() instanceof BreakendVariant bv) {
            setGenomicAssembly(variant.genomicAssembly());
            eventIdTextField.setText(bv.eventId());
            leftBreakendController.presentComponent(bv.left());
            rightBreakendController.presentComponent(bv.right());

            referenceSequenceTextField.setText(bv.ref());
            insertedSequenceTextField.setText(bv.alt());
        } else {
            LOGGER.warn("Not presenting non-breakend variant {}", variant);
        }
    }

    @Override
    public CuratedVariant getComponent() throws InvalidComponentDataException {
        Variant variant = Variant.of(eventIdTextField.getText(), leftBreakendController.getComponent(), rightBreakendController.getComponent(), referenceSequenceTextField.getText(), insertedSequenceTextField.getText());

        return CuratedVariant.of(getGenomicAssembly(), variant, getVariantMetadata());
    }

}
