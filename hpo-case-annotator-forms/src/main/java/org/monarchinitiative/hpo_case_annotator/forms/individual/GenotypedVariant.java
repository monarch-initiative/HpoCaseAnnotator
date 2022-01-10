package org.monarchinitiative.hpo_case_annotator.forms.individual;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.util.Objects;

public class GenotypedVariant {

    private final CuratedVariant curatedVariant;

    private final ObjectProperty<Genotype> genotype = new SimpleObjectProperty<>(this, "genotype");

    public static GenotypedVariant of(CuratedVariant curatedVariant) {
        return of(curatedVariant, null);
    }

    public static GenotypedVariant of(CuratedVariant curatedVariant, Genotype genotype) {
        return new GenotypedVariant(curatedVariant, genotype);
    }

    private GenotypedVariant(CuratedVariant curatedVariant, Genotype genotype) {
        this.curatedVariant = Objects.requireNonNull(curatedVariant, "Curated variant must not be null");
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

    public CuratedVariant curatedVariant() {
        return curatedVariant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenotypedVariant) obj;
        return Objects.equals(this.curatedVariant, that.curatedVariant) &&
                Objects.equals(this.genotype, that.genotype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curatedVariant, genotype);
    }

    @Override
    public String toString() {
        return "GenotypedVariant[" +
                "variant=" + curatedVariant + ", " +
                "genotype=" + genotype.get() + ']';
    }

}
