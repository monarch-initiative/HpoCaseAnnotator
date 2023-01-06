package org.monarchinitiative.hpo_case_annotator.forms.variants.input;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.monarchinitiative.hpo_case_annotator.forms.util.FormUtils;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.svart.Contig;

import java.net.URL;
import java.util.stream.Stream;

/**
 * The controller manages contig and variant ID for
 * {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SEQUENCE} and
 * {@link org.monarchinitiative.hpo_case_annotator.observable.v2.VariantNotation#SYMBOLIC} variants.
 */
abstract class VcfSequenceOrSymbolicVariantDataEdit extends BaseVariantDataEdit {

    @FXML
    protected ComboBox<Contig> contigComboBox;

    @FXML
    protected TextField idTextField;

    protected VcfSequenceOrSymbolicVariantDataEdit(URL location) {
        super(location);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        FormUtils.initializeContigComboBox(contigComboBox, genomicAssemblyService);
    }

    @Override
    public void setInitialData(ObservableCuratedVariant data) {
        super.setInitialData(data);

        contigComboBox.setValue(data.getContig());
        idTextField.setText(data.getId());
    }

    @Override
    public void commit() {
        super.commit();

        item.setContig(contigComboBox.getValue());
        item.setId(idTextField.getText());
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of(contigComboBox.valueProperty(), idTextField.textProperty());
    }
}
