package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javafx.scene.control.TextFormatter;
import org.monarchinitiative.hpo_case_annotator.forms.util.TextFormatters;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation;
import org.monarchinitiative.svart.GenomicVariant;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation of {@link org.monarchinitiative.hpo_case_annotator.forms.DataEdit} protocol for
 * {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SEQUENCE} variants.
 */
public class VcfSequenceVariantDataEdit extends VcfSequenceOrSymbolicVariantDataEdit {

    @FXML
    private TextField positionTextField;
    private final TextFormatter<Integer> positionTextFormatter = TextFormatters.positiveIntegerFormatter();
    @FXML
    private TextField referenceTextField;
    private final TextFormatter<String> referenceTextFormatter = TextFormatters.vcfAlleleFormatter();
    @FXML
    private TextField alternativeTextField;
    private final TextFormatter<String> alternativeTextFormatter = TextFormatters.vcfAlleleFormatter();

    public VcfSequenceVariantDataEdit() {
        super(VcfSequenceVariantDataEdit.class.getResource("VcfSequenceVariantDataEdit.fxml"));

        // Unusually, we do not set this in initialize.
        positionTextField.setTextFormatter(positionTextFormatter);
        referenceTextField.setTextFormatter(referenceTextFormatter);
        alternativeTextField.setTextFormatter(alternativeTextFormatter);
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);
        positionTextFormatter.setValue(data.getStart());
        referenceTextFormatter.setValue(data.getRef());
        alternativeTextFormatter.setValue(data.getAlt());
    }

    @Override
    public void commit() {
        super.commit();

        item.setVariantNotation(VariantNotation.SEQUENCE);
        String ref = referenceTextFormatter.getValue();
        int start = positionTextFormatter.getValue();
        int end = start + ref.length() - 1;
        item.setStart(start);
        item.setEnd(end);
        item.setRef(ref);
        item.setAlt(alternativeTextFormatter.getValue());
    }

    @Override
    protected Stream<Observable> dependencies() {
        Stream<Observable> dependencies = Stream.of(
                positionTextField.textProperty(),
                referenceTextField.textProperty(),
                alternativeTextField.textProperty()
        );

        return Stream.concat(super.dependencies(), dependencies);
    }

    @Override
    protected Optional<GenomicVariant> getVariant() {
        try {
            GenomicVariant gv = GenomicVariant.of(contigComboBox.getValue(),
                    idTextField.getText(),
                    CuratedVariant.VCF_STRAND,
                    CuratedVariant.VCF_COORDINATE_SYSTEM,
                    positionTextFormatter.getValue(),
                    referenceTextFormatter.getValue(),
                    alternativeTextFormatter.getValue());
            return Optional.of(gv);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
