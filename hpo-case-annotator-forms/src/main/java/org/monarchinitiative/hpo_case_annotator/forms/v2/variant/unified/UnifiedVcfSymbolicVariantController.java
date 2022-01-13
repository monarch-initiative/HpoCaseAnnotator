package org.monarchinitiative.hpo_case_annotator.forms.v2.variant.unified;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.core.reference.DeprecatedGenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.util.Formats;
import org.monarchinitiative.hpo_case_annotator.forms.util.VariantTypeStringConverter;
import org.monarchinitiative.svart.VariantType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class UnifiedVcfSymbolicVariantController extends VcfSeqSymVariantController {

    private static final Set<VariantType> IGNORED = Set.of(VariantType.UNKNOWN, VariantType.SYMBOLIC, VariantType.TRA, VariantType.STR);

    @FXML
    private TextField startTextField;
    @FXML
    private TextField endTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private ComboBox<VariantType> altComboBox;

    private ObjectBinding<Integer> startBinding;
    private ObjectBinding<Integer> endBinding;

    public UnifiedVcfSymbolicVariantController(DeprecatedGenomicAssemblyRegistry genomicAssemblyRegistry) {
        super(genomicAssemblyRegistry);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        startTextField.setTextFormatter(Formats.numberFormatter());
        endTextField.setTextFormatter(Formats.numberFormatter());
        initializeAltVariantTypes();

        startBinding = Bindings.createObjectBinding(() -> processFormattedInteger(startTextField.getText()).orElse(null), startTextField.textProperty());
        endBinding = Bindings.createObjectBinding(() -> processFormattedInteger(endTextField.getText()).orElse(null), endTextField.textProperty());
    }

    @Override
    protected ChangeListener<ObservableSeqSymVariant> variantChangeListener() {
        return (obs, old, novel) -> {
            if (old != null) unbind(old);
            if (novel != null) bind(novel);
        };
    }

    protected void unbind(ObservableSeqSymVariant variant) {
        super.unbind(variant);

        variant.startProperty().unbind();
        variant.endProperty().unbind();
        referenceTextField.textProperty().unbindBidirectional(variant.refProperty());

        altComboBox.valueProperty().unbindBidirectional(variant.variantTypeProperty());
    }

    protected void bind(ObservableSeqSymVariant variant) {
        super.bind(variant);
        startTextField.setText(String.valueOf(variant.getStart()));
        variant.startProperty().bind(startBinding);
        endTextField.setText(String.valueOf(variant.getEnd()));
        variant.endProperty().bind(endBinding);

        referenceTextField.textProperty().bindBidirectional(variant.refProperty());
        altComboBox.valueProperty().bindBidirectional(variant.variantTypeProperty());
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
