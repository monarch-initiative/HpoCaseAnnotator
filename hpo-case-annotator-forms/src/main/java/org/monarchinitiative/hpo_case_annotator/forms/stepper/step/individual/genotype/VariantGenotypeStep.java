package org.monarchinitiative.hpo_case_annotator.forms.stepper.step.individual.genotype;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import org.monarchinitiative.hpo_case_annotator.forms.stepper.BaseStep;
import org.monarchinitiative.hpo_case_annotator.forms.variants.VariantGenotypeTable;
import org.monarchinitiative.hpo_case_annotator.observable.v2.BaseObservableIndividual;
import org.monarchinitiative.hpo_case_annotator.observable.v2.ObservableCuratedVariant;

import java.util.stream.Stream;

public class VariantGenotypeStep <T extends BaseObservableIndividual> extends BaseStep<T> {

    private final ListProperty<ObservableCuratedVariant> variants = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private VariantGenotypeTable genotypeTable;

    public VariantGenotypeStep() {
        super(VariantGenotypeStep.class.getResource("VariantGenotypeStep.fxml"));
        genotypeTable.dataProperty().bind(data);
        genotypeTable.variantsProperty().bind(variants);
    }

    public ListProperty<ObservableCuratedVariant> variantsProperty() {
        return variants;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected Stream<Observable> dependencies() {
        return Stream.of();
    }

    @Override
    public void invalidated(Observable observable) {
        // no-op as of now
    }

    @Override
    protected void bind(T data) {
        // no-op as of now
    }

    @Override
    protected void unbind(T data) {
        // no-op as of now
    }
}
