package org.monarchinitiative.hpo_case_annotator.forms.stepper.step;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import org.monarchinitiative.hpo_case_annotator.forms.FunctionalAnnotationRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.GenomicAssemblyRegistry;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantSummary;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableStudy;

import java.util.stream.Stream;

/**
 * <h2>Properties</h2>
 * <ul>
 *     <li>{@link #variantsProperty()}</li>
 *     <li>{@link #genomicAssemblyRegistryProperty()}</li>
 *     <li>{@link #functionalAnnotationRegistryProperty()}</li>
 * </ul>
 */
public class VariantsStep<T extends ObservableStudy> extends BaseStep<T> {

    private final ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistry = new SimpleObjectProperty<>();
    private final ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistry = new SimpleObjectProperty<>();

    @FXML
    private VariantSummary variantSummary;

    public VariantsStep() {
        super(VariantsStep.class.getResource("VariantsStep.fxml"));

        // This indeed is an unusual place for setting up bindings.
        // However, note that we cannot use `initialize` method.
        // The fields of this class are `null` when `initialize` is called in the super constructor.
        variantSummary.genomicAssemblyRegistryProperty().bind(genomicAssemblyRegistry);
        variantSummary.functionalAnnotationRegistryProperty().bind(functionalAnnotationRegistry);
    }

    public ObjectProperty<ObservableList<ObservableCuratedVariant>> variantsProperty() {
        return variantSummary.variants();
    }

    public ObjectProperty<GenomicAssemblyRegistry> genomicAssemblyRegistryProperty() {
        return genomicAssemblyRegistry;
    }

    public ObjectProperty<FunctionalAnnotationRegistry> functionalAnnotationRegistryProperty() {
        return functionalAnnotationRegistry;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected Stream<Observable> dependencies() {
        // TODO - implement
        return Stream.of();
    }

    @Override
    public void invalidated(Observable obs) {
        // TODO - implement
    }

    @Override
    public Parent getContent() {
        return this;
    }

    @Override
    protected void bind(T data) {
        if (data != null)
            variantSummary.variants().bindBidirectional(data.variantsProperty());
        else
            variantSummary.variants().get().clear();
    }

    @Override
    protected void unbind(T data) {
        if (data != null)
            variantSummary.variants().unbindBidirectional(data.variantsProperty());
    }
}
