package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.VariantTypeStringConverter;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.svart.Variant;
import org.monarchinitiative.svart.VariantType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class VcfSymbolicVariantController extends VcfSeqSymVariantController {

    private static final Set<VariantType> IGNORED = Set.of(VariantType.UNKNOWN, VariantType.SYMBOLIC, VariantType.TRA, VariantType.STR);

    @FXML
    private TextField startTextField;
    @FXML
    private TextField endTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private ComboBox<VariantType> altComboBox;

    public VcfSymbolicVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        startTextField.setTextFormatter(Formats.numberFormatter());
        endTextField.setTextFormatter(Formats.numberFormatter());
        initializeAltVariantTypes();
    }


    @Override
    public void presentComponent(CuratedVariant variant) {
        setGenomicAssembly(variant.genomicAssembly());
        setContig(variant.contig());
        setVariantId(variant.id());
        startTextField.setText(String.valueOf(variant.startOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM)));
        endTextField.setText(String.valueOf(variant.endOnStrandWithCoordinateSystem(VCF_STRAND, VCF_COORDINATE_SYSTEM)));
        referenceTextField.setText(variant.ref());
        altComboBox.setValue(VariantType.parseType(variant.ref(), variant.alt()));
    }

    @Override
    public CuratedVariant getComponent() throws InvalidComponentDataException {
        int start = FormUtils.processFormattedInteger(startTextField.getText());
        int end = FormUtils.processFormattedInteger(endTextField.getText());
        int changeLength = calculateChangeLength(altComboBox.getValue(), start, end);

        Variant variant = Variant.of(getContig(),
                getVariantId(),
                VCF_STRAND,
                VCF_COORDINATE_SYSTEM,
                start,
                end,
                referenceTextField.getText(),
                String.format("<%s>", altComboBox.getValue().baseType()),
                changeLength);

        return CuratedVariant.of(getGenomicAssembly(), variant, getVariantMetadata());
    }

    private int calculateChangeLength(VariantType value, int start, int end) throws InvalidComponentDataException {
        int change = end - start + 1;
        return switch (value.baseType()) {
            case SNV, DUP, INS, CNV -> change;
            case DEL -> -change;
            case INV -> 0;
            default -> throw new InvalidComponentDataException("Unexpected variant type: " + value.baseType());
        };
    }

    private void initializeAltVariantTypes() {
        altComboBox.setConverter(VariantTypeStringConverter.getInstance());
        Arrays.stream(VariantType.values())
                .map(VariantType::baseType)
                .filter(vt -> !IGNORED.contains(vt))
                .distinct()
                .sorted(Comparator.comparing(Enum::name))
                .forEachOrdered(vt -> altComboBox.getItems().add(vt));
    }
}
