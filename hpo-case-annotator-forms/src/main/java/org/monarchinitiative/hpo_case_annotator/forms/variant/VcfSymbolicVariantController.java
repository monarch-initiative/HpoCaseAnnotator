package org.monarchinitiative.hpo_case_annotator.forms.variant;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.format.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.format.VariantTypeStringConverter;
import org.monarchinitiative.svart.VariantType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class VcfSymbolicVariantController extends VcfBaseVariantController {

    private static final Set<VariantType> IGNORED = Set.of(VariantType.UNKNOWN, VariantType.SYMBOLIC, VariantType.TRA, VariantType.STR);

    public TextField startTextField;
    public TextField endTextField;
    public TextField referenceTextField;
    public ComboBox<VariantType> altComboBox;

    public VcfSymbolicVariantController(GenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    public void initialize() {
        super.initialize();
        startTextField.setTextFormatter(Formats.numberFormatter());
        endTextField.setTextFormatter(Formats.numberFormatter());
        initializeAltVariantTypes();
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
