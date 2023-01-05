package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.forms.util.converters.VariantTypeStringConverter;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation;
import org.monarchinitiative.svart.GenomicVariant;
import org.monarchinitiative.svart.VariantType;

import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of {@link org.monarchinitiative.hpo_case_annotator.forms.DataEdit} protocol for
 * {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SYMBOLIC} variants.
 */
public class VcfSymbolicVariantDataEdit extends VcfSequenceOrSymbolicVariantDataEdit {

    private static final Set<VariantType> IGNORED = Set.of(VariantType.UNKNOWN, VariantType.SYMBOLIC, VariantType.TRA, VariantType.STR);

    @FXML
    private TextField startTextField;
    private final TextFormatter<Integer> startTextFormatter = TextFormatters.nonNegativeIntegerFormatter();
    @FXML
    private TextField endTextField;
    private final TextFormatter<Integer> endTextFormatter = TextFormatters.positiveIntegerFormatter();
    @FXML
    private ComboBox<VariantType> altComboBox;

    public VcfSymbolicVariantDataEdit() {
        super(VcfSymbolicVariantDataEdit.class.getResource("VcfSymbolicVariantDataEdit.fxml"));

        // Unusually, we do not set this in initialize.
        startTextField.setTextFormatter(startTextFormatter);
        endTextField.setTextFormatter(endTextFormatter);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        initializeAltVariantTypes();
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);

        startTextFormatter.setValue(data.getStart());
        endTextFormatter.setValue(data.getEnd());
        altComboBox.setValue(data.getVariantType());
    }

    @Override
    public void commit() {
        super.commit();

        item.setVariantNotation(VariantNotation.SYMBOLIC);
        item.setStart(startTextFormatter.getValue());
        item.setEnd(endTextFormatter.getValue());

        item.setRef("N");
        item.setAlt(altComboBox.getValue().toString());
        item.setVariantType(altComboBox.getValue());
    }

    @Override
    protected Stream<Observable> dependencies() {
        Stream<Observable> dependencies = Stream.of(
                startTextField.textProperty(),
                endTextField.textProperty(),
                altComboBox.valueProperty());
        return Stream.concat(super.dependencies(), dependencies);
    }

    @Override
    protected Optional<GenomicVariant> getVariant() {
        // Always empty since functional annotation is not (yet) supported.
        return Optional.empty();
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

    private int calculateChangeLength(VariantType value, int start, int end) throws InvalidComponentDataException {
        int change = end - start + 1;
        return switch (value.baseType()) {
            case SNV, DUP, INS, CNV -> change;
            case DEL -> -change;
            case INV -> 0;
            default -> throw new InvalidComponentDataException("Unexpected variant type: " + value.baseType());
        };
    }

}
