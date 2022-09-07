package org.monarchinitiative.hpo_case_annotator.forms.variants.detail;

import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.svart.Contig;

import java.net.URL;

import static javafx.beans.binding.Bindings.*;

/**
 * Variants in {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SEQUENCE} and
 * {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SYMBOLIC} share variant ID and contig.
 */
abstract class SequenceSymbolicVariantDetail extends BaseVariantDetail {

    @FXML
    private TitledLabel variantId;
    @FXML
    private TitledLabel contig;

    public SequenceSymbolicVariantDetail(URL location) {
        super(location);
    }

    @FXML
    @Override
    protected void initialize() {
        super.initialize();

        variantId.textProperty().bind(selectString(data, "id"));

        ObjectBinding<Contig> cb = select(data, "contig");
        contig.textProperty().bind(
                createStringBinding(
                        () -> cb.get() == null ? "N/A": cb.get().name(),
                        cb)
        );
    }
}
