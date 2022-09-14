package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.util.stream.Stream;

public class ObservableVariantGenotype extends ObservableItem implements VariantGenotype {

    public static final Callback<ObservableVariantGenotype, Observable[]> EXTRACTOR = obs -> new Observable[]{obs.genotype};
    private String md5Hex;
    private final ObjectProperty<Genotype> genotype = new SimpleObjectProperty<>(this, "genotype", Genotype.UNSET);

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
    public Stream<Observable> observables() {
        return Stream.of(genotype);
    }

    @Override
    public String toString() {
        return "ObservableVariantGenotype{" +
                "md5Hex='" + md5Hex + '\'' +
                ", genotype=" + genotype.get() +
                '}';
    }
}
