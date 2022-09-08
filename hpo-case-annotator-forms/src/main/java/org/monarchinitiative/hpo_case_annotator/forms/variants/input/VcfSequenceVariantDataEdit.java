package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
    @FXML
    private TextField referenceTextField;
    @FXML
    private TextField alternativeTextField;

    public VcfSequenceVariantDataEdit() {
        super(VcfSequenceVariantDataEdit.class.getResource("VcfSequenceVariantDataEdit.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);

        positionTextField.setText(String.valueOf(data.getStart()));
        referenceTextField.setText(data.getRef());
        alternativeTextField.setText(data.getAlt());
    }

    @Override
    public void commit() {
        super.commit();

        item.setVariantNotation(VariantNotation.SEQUENCE);
        // TODO - check this is OK
        int value = Integer.parseInt(positionTextField.getText());
        item.setStart(value);
        item.setRef(referenceTextField.getText());
        item.setAlt(alternativeTextField.getText());
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
            // TODO - prettify
            int position = Integer.parseInt(positionTextField.getText());
            GenomicVariant gv = GenomicVariant.of(contigComboBox.getValue(),
                    idTextField.getText(),
                    CuratedVariant.VCF_STRAND,
                    CuratedVariant.VCF_COORDINATE_SYSTEM,
                    position,
                    referenceTextField.getText(),
                    alternativeTextField.getText());
            return Optional.of(gv);
        } catch (Exception e) {
            // TODO - handle properly
            return Optional.empty();
        }
    }

}
