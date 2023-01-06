package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

    // Note - the set must be kept in sync with calculateChangeLength function
    private static final Set<VariantType> IGNORED = Set.of(VariantType.UNKNOWN,
            VariantType.SNV,
            VariantType.MNV,
            VariantType.BND,
            VariantType.TRA,
            VariantType.STR,
            VariantType.SYMBOLIC);

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
        item.setAlt("<%s>".formatted(altComboBox.getValue()));
        item.setVariantType(altComboBox.getValue());
        int changeLength = calculateChangeLength(altComboBox.getValue(),
                startTextFormatter.getValue(),
                endTextFormatter.getValue());
        item.setChangeLength(changeLength);
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

    private int calculateChangeLength(VariantType value, int start, int end) {
        int change = end - start + 1;
        return switch (value.baseType()) {
            case INV -> 0;
            case DUP, DUP_TANDEM, DUP_INV_BEFORE, DUP_INV_AFTER,
                    CNV, CNV_COMPLEX, CNV_GAIN, CNV_LOSS, CNV_LOH,
                    INS, INS_ME, INS_ME_ALU, INS_ME_LINE1, INS_ME_SVA, INS_ME_HERV -> change;
            case DEL, DEL_ME_ALU, DEL_ME, DEL_ME_LINE1, DEL_ME_SVA, DEL_ME_HERV -> -change;
            // This is UI for symbolic variants, hence no breakends!
            // Note - the branch must be kept in sync with `IGNORED` set
            case UNKNOWN, SNV, MNV, BND, TRA, STR, SYMBOLIC -> throw new IllegalArgumentException("Illegal variant type %s".formatted(value));
        };
    }

}
