package org.monarchinitiative.hpo_case_annotator.observable.v2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

public class GenotypedVariant {

    private final ObservableCuratedVariant curatedVariant;

    private final ObjectProperty<Genotype> genotype = new SimpleObjectProperty<>(this, "genotype");

    public GenotypedVariant(CuratedVariant curatedVariant) {
        this(curatedVariant, null);
    }

    public GenotypedVariant(CuratedVariant curatedVariant, Genotype genotype) {
        this.curatedVariant = new ObservableCuratedVariant(curatedVariant);
        this.genotype.set(genotype);
    }

    public Genotype getGenotype() {
        return genotype.get();
    }

    public ObjectProperty<Genotype> genotypeProperty() {
        return genotype;
    }

    public void setGenotype(Genotype genotype) {
        this.genotype.set(genotype);
    }

    public ObservableCuratedVariant getCuratedVariant() {
        return curatedVariant;
    }

    @Override
    public String toString() {
        return "GenotypedVariant[" +
                "variant=" + curatedVariant + ", " +
                "genotype=" + genotype.get() + ']';
    }

}
