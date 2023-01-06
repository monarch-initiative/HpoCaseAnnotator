package org.monarchinitiative.hpo_case_annotator.forms.variants.detail;

import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;

import static javafx.beans.binding.Bindings.*;

/**
 * Read-only presentation of {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SYMBOLIC}
 * variants.
 */
public class SymbolicVariantDetail extends SequenceSymbolicVariantDetail {

    @FXML
    private TitledLabel start;
    @FXML
    private TitledLabel end;
    @FXML
    private TitledLabel ref;
    @FXML
    private TitledLabel alt;

    public SymbolicVariantDetail() {
        super(SymbolicVariantDetail.class.getResource("SymbolicVariantDetail.fxml"));
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();

        start.textProperty().bind(selectInteger(data, "start").asString());
        end.textProperty().bind(selectInteger(data, "end").asString());
        ref.textProperty().bind(selectString(data, "ref"));
        alt.textProperty().bind(selectString(data, "alt"));
    }
}
