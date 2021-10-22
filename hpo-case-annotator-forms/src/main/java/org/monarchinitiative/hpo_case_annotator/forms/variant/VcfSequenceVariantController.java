package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;

public class VcfSequenceVariantController extends VcfBaseVariantController {

    public TextField positionTextField;
    public TextField referenceTextField;
    public TextField alternativeTextField;

    public VcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }


    public void initialize() {
        super.initialize();
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }


}
