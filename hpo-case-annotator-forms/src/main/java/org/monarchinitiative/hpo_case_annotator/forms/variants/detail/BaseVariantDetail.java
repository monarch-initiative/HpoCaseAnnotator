package org.monarchinitiative.hpo_case_annotator.forms.variants.detail;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.TitledLabel;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;

import java.io.IOException;
import java.net.URL;

import static javafx.beans.binding.Bindings.*;

/**
 * {@link BaseVariantDetail} and the subclasses present single {@link ObservableCuratedVariant} in a read-only fashion.
 * <p>
 * The class manages genomic assembly for all variant notations.
 */
abstract class BaseVariantDetail extends VBoxObservableDataController<ObservableCuratedVariant> {

    @FXML
    private TitledLabel assembly;

    public BaseVariantDetail(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void initialize() {
        assembly.textProperty().bind(selectString(data, "genomicAssembly"));
    }

}
