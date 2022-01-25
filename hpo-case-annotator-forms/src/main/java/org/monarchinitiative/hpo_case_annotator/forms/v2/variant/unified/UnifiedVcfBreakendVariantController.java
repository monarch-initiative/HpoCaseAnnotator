package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.monarchinitiative.hpo_case_annotator.core.reference.genome.DeprecatedGenomicAssemblyRegistry;

public class UnifiedVcfBreakendVariantController extends UnifiedBaseVariantController<ObservableBreakendVariant> {

    @FXML
    private VBox leftBreakend;
    @FXML
    private UnifiedBreakendController leftBreakendController;
    @FXML
    private VBox rightBreakend;
    @FXML
    private UnifiedBreakendController rightBreakendController;

    @FXML
    private TextField insertedSequenceTextField;

    public UnifiedVcfBreakendVariantController(DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        leftBreakendController.assemblyServiceProperty().bind(assemblyService);
        rightBreakendController.assemblyServiceProperty().bind(assemblyService);
    }

    public ChangeListener<ObservableBreakendVariant> variantChangeListener() {
        return (obs, old, novel) -> {
            // TODO - implement
        };
    }

}
