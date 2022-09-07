package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;

import java.io.IOException;
import java.net.URL;

abstract class VariantGenotypesObservableData<T extends BaseObservableIndividual> extends VBoxObservableDataController<T> {

    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;

    @FXML
    private VariantGenotypeTable variantGenotypeTable;

    protected VariantGenotypesObservableData(URL location) {
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
        individualIds.dataProperty().bind(data);

        variantGenotypeTable.dataProperty().bind(data);
        variantGenotypeTable.variantsProperty().bind(variants);
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

}
