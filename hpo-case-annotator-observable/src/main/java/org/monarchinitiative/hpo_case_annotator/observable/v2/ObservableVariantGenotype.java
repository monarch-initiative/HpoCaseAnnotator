package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

public class ObservableVariantGenotype implements VariantGenotype, Observable {

    static final Callback<ObservableVariantGenotype, Observable[]> EXTRACTOR = ovg -> new Observable[]{ovg.genotype};
    private String id;
    private final ObjectProperty<Genotype> genotype = new SimpleObjectProperty<>(this, "genotype");

    public ObservableVariantGenotype() {
    }

    public ObservableVariantGenotype(VariantGenotype variantGenotype) {
        if (variantGenotype != null) {
            id = variantGenotype.getId();
            genotype.set(variantGenotype.getGenotype());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Genotype getGenotype() {
        return genotype.get();
    }

    public void setGenotype(Genotype genotype) {
        this.genotype.set(genotype);
    }

    public ObjectProperty<Genotype> genotypeProperty() {
        return genotype;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this)) {
            observable.addListener(listener);
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        for (Observable observable : EXTRACTOR.call(this)) {
            observable.addListener(listener);
        }
    }

    @Override
    public String toString() {
        return "ObservableVariantGenotype{" +
                "id='" + id + '\'' +
                ", genotype=" + genotype.get() +
                '}';
    }
}
