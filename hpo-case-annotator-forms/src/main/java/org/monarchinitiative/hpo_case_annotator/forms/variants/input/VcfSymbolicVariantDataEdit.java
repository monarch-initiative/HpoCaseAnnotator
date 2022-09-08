package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.InvalidComponentDataException;
import org.monarchinitiative.hpo_case_annotator.forms.util.VariantTypeStringConverter;
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
    @FXML
    private TextField endTextField;
    @FXML
    private TextField referenceTextField;
    @FXML
    private ComboBox<VariantType> altComboBox;

    public VcfSymbolicVariantDataEdit() {
        super(VcfSymbolicVariantDataEdit.class.getResource("VcfSymbolicVariantDataEdit.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();
        initializeAltVariantTypes();
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);

        startTextField.setText(String.valueOf(data.getStart()));
        endTextField.setText(String.valueOf(data.getEnd()));
        referenceTextField.setText(data.getRef());
        altComboBox.setValue(data.getVariantType());
    }

    @Override
    public void commit() {
        super.commit();

        item.setVariantNotation(VariantNotation.SYMBOLIC);
        // TODO - check this is OK
        item.setStart(Integer.parseInt(startTextField.getText()));
        item.setEnd(Integer.parseInt(endTextField.getText()));

        item.setRef(referenceTextField.getText());
        item.setVariantType(altComboBox.getValue());
    }

    @Override
    protected Stream<Observable> dependencies() {
        Stream<Observable> dependencies = Stream.of(
                startTextField.textProperty(),
                endTextField.textProperty(),
                referenceTextField.textProperty(),
                altComboBox.valueProperty());
        return Stream.concat(super.dependencies(), dependencies);
    }

    @Override
    protected Optional<GenomicVariant> getVariant() {
        // TODO - implement
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
