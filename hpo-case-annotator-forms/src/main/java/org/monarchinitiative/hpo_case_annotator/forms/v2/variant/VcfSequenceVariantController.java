package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.FunctionalAnnotationRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.GenomicVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class VcfSequenceVariantController extends VcfSeqSymVariantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VcfSequenceVariantController.class);

    @FXML
    private TextField positionTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private TextField alternativeTextField;
    public VcfSequenceVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry,
                                        FunctionalAnnotationRegistry functionalAnnotationRegistry) {
        super(genomicAssemblyRegistry, functionalAnnotationRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        positionTextField.setTextFormatter(Formats.numberFormatter());
    }

    @Override
    protected List<Observable> variantInputFields() {
        LinkedList<Observable> observables = new LinkedList<>(super.variantInputFields());
        observables.add(positionTextField.textProperty());
        observables.add(referenceTextField.textProperty());
        observables.add(alternativeTextField.textProperty());
        return observables;
    }

    /**
     * Present sequence variant.
     * @param variant sequence variant
     */
    @Override
    public void presentComponent(CuratedVariant variant) {
        setGenomicAssembly(variant.getGenomicAssembly());
        setContig(variant.getVariant().contig());
        setVariantId(variant.id());
        positionTextField.setText(String.valueOf(variant.getVariant().startOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM)));
        referenceTextField.setText(variant.getVariant().ref());
        alternativeTextField.setText(variant.getVariant().alt());
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
