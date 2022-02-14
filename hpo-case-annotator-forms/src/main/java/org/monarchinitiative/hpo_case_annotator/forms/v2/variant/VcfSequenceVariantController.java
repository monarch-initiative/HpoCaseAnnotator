package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.GenomicVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcfSequenceVariantController extends VcfSeqSymVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcfSequenceVariantController.class);

    @FXML
    private TextField positionTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private TextField alternativeTextField;

    public VcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }


    /**
     * Present sequence variant.
     * @param variant sequence variant
     */
    @Override
    public void presentComponent(CuratedVariant variant) {
        setGenomicAssembly(variant.genomicAssembly());
        setContig(variant.contig());
        setVariantId(variant.id());
        positionTextField.setText(String.valueOf(variant.startOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM)));
        referenceTextField.setText(variant.ref());
        alternativeTextField.setText(variant.alt());
    }

    @Override
    public CuratedVariant getComponent() throws InvalidComponentDataException {
        int pos = FormUtils.processFormattedInteger(positionTextField.getText());

        GenomicVariant variant = GenomicVariant.of(getContig(),
                getVariantId(),
                VCF_STRAND,
                VCF_COORDINATE_SYSTEM,
                pos,
                referenceTextField.getText(),
                alternativeTextField.getText());

        return CuratedVariant.of(getGenomicAssembly(), variant, getVariantMetadata());
    }
}
