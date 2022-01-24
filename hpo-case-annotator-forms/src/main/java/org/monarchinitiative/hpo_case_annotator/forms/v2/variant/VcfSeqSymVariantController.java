package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.svart.Contig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The controller manages contig for sequence and symbolic variants.
 */
public abstract class VcfSeqSymVariantController extends BaseVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcfSeqSymVariantController.class);

    @FXML
    private ComboBox<Contig> contigComboBox;

    @FXML
    private TextField idTextField;

    @FXML
    protected void initialize() {
        super.initialize();
        FormUtils.initializeContigComboBox(contigComboBox, assemblyService);
    }

    protected VcfSeqSymVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    protected void setContig(Contig contig) {
        contigComboBox.setValue(contig);
    }

    protected Contig getContig() {
        return contigComboBox.getValue();
    }

    protected void setVariantId(String variantId) {
        idTextField.setText(variantId);
    }

    protected String getVariantId() {
        return idTextField.getText();
    }
}
