package org.monarchinitiative.hpo_case_annotator.forms.variants.detail;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;

import static javafx.beans.binding.Bindings.*;

/**
 * Read-only presentation of {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SEQUENCE}
 * variants.
 */
public class SequenceVariantDetail extends SequenceSymbolicVariantDetail {

    @FXML
    private TitledLabel pos;
    @FXML
    private TitledLabel ref;
    @FXML
    private TitledLabel alt;

    public SequenceVariantDetail() {
        super(SequenceVariantDetail.class.getResource("SequenceVariantDetail.fxml"));
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();

        pos.textProperty().bind(selectInteger(data, "start").asString());
        ref.textProperty().bind(selectString(data, "ref"));
        alt.textProperty().bind(selectString(data, "alt"));
    }

}
