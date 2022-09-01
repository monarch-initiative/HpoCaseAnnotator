package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.HCAControllerFactory;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

public class VariantsStep<T extends ObservableStudy> extends BaseStep<T> {

    @FXML
    private VariantSummary variantSummary;

    public VariantsStep(HCAControllerFactory controllerFactory) {
        super(VariantsStep.class.getResource("VariantsStep.fxml"));
//        variantSummary.setControllerFactory(controllerFactory);
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        variantSummary.variants().bindBidirectional(data.variants());
    }

    @Override
    protected void unbind(T data) {
        variantSummary.variants().unbindBidirectional(data.variants());
    }

    public ObjectProperty<ObservableList<CuratedVariant>> variantsProperty() {
        return variantSummary.variants();
    }

}
