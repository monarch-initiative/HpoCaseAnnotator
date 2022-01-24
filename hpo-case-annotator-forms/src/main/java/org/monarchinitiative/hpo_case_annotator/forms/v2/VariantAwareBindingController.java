package org.monarchinitiative.hpo_case_annotator.forms.v2;

import javafx.collections.ObservableList;
import org.monarchinitiative.hpo_case_annotator.forms.BindingDataController;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;

/**
 * The controller that ensures the indiviual also knows about an observable collection of curated variants.
 * @param <T>
 */
public abstract class VariantAwareBindingController<T extends BaseObservableIndividual> extends BindingDataController<T> {

    public abstract ObservableList<CuratedVariant> curatedVariants();
}
