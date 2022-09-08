package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableGenomicBreakend;
import org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation;
import org.monarchinitiative.svart.GenomicVariant;

import java.util.Optional;
import java.util.stream.Stream;

public class VcfBreakendVariantDataEdit extends BaseVariantDataEdit {

    // TODO - to implement

    @FXML
    private TextField eventIdTextField;
    @FXML
    private GenomicBreakendDataEdit left;
    @FXML
    private GenomicBreakendDataEdit right;
    @FXML
    private TextField referenceSequenceTextField;

    @FXML
    private TextField insertedSequenceTextField;

    public VcfBreakendVariantDataEdit() {
        super(VcfBreakendVariantDataEdit.class.getResource("VcfBreakendVariantDataEdit.fxml"));
    }

    @FXML
    protected void initialize() {
        super.initialize();

        left.genomicAssemblyServiceProperty().bind(genomicAssemblyService);
        right.genomicAssemblyServiceProperty().bind(genomicAssemblyService);
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);

        eventIdTextField.setText(data.getId());

        if (data.getLeft() == null)
            data.setLeft(new ObservableGenomicBreakend());
        left.setInitialData(data.getLeft());

        if (data.getRight() == null)
            data.setRight(new ObservableGenomicBreakend());
        right.setInitialData(data.getRight());

        referenceSequenceTextField.setText(data.getRef());
        insertedSequenceTextField.setText(data.getAlt());
    }

    @Override
    public void commit() {
        super.commit();

        item.setVariantNotation(VariantNotation.BREAKEND);
        // TODO - implement commit
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(
                eventIdTextField.textProperty(),
                left, right,
                referenceSequenceTextField.textProperty(), insertedSequenceTextField.textProperty()
        );
    }

    @Override
    protected Optional<GenomicVariant> getVariant() {
        // TODO - implement
        return Optional.empty();
    }

}
