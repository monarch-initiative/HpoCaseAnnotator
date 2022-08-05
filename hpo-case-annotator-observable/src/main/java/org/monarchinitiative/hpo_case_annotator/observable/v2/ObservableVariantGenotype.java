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
    private String md5Hex;
    private final ObjectProperty<Genotype> genotype = new SimpleObjectProperty<>(this, "genotype");

    public ObservableVariantGenotype() {
    }

    public ObservableVariantGenotype(VariantGenotype variantGenotype) {
        if (variantGenotype != null) {
            md5Hex = variantGenotype.getMd5Hex();
            genotype.set(variantGenotype.getGenotype());
        }
    }

    @Override
    public String getMd5Hex() {
        return md5Hex;
    }

    public void setMd5Hex(String md5Hex) {
        this.md5Hex = md5Hex;
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
                "md5Hex='" + md5Hex + '\'' +
                ", genotype=" + genotype.get() +
                '}';
    }
}
