package org.monarchinitiative.hpo_case_annotator.forms.variants;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.VBoxObservableDataController;
import org.monarchinitiative.hpo_case_annotator.forms.component.BaseIndividualIdsComponent;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;


class VariantDataEdit<T extends BaseObservableIndividual> extends VBoxObservableDataController<T> {

    private final ListProperty<CuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private BaseIndividualIdsComponent<BaseObservableIndividual> individualIds;

    @FXML
    private VariantGenotypeTable variantGenotypeTable;

    @FXML
    protected void initialize() {
        individualIds.dataProperty().bind(data);

        variantGenotypeTable.dataProperty().bind(data);
        variantGenotypeTable.variantsProperty().bind(variants);
    }

    public ListProperty<CuratedVariant> variantsProperty() {
        return variants;
    }

}
