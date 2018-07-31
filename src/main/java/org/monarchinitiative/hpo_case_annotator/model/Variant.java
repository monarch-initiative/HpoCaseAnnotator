package org.monarchinitiative.hpo_case_annotator.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean representing base attributes of each biocurated variant.<p> These attributes are being stored at the moment:
 * <ul> <li>chromosome</li> <li>position</li> <li>reference allele</li> <li>alternate allele</li> <li>snippet</li>
 * <li>genotype</li> <li>variant class</li> <li>pathomechanism</li> <li>cosegregation</li> <li>comparability</li> </ul>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class Variant implements Comparable<Variant> {

    /* This needs to be initialized in subclass */
    protected VariantMode variantMode;


    /* Chromosome*/
    private StringProperty chromosome = new SimpleStringProperty(this, "chromosome");

    /* Chromosomal position property. Note that it is not IntegerProperty
       because it needs to be synchronized with {@link javafx.scene.control.TextField} field */
    private StringProperty position = new SimpleStringProperty(this, "position");

    /* Reference allele */
    private StringProperty referenceAllele = new SimpleStringProperty(this, "referenceAllele");

    /* Alternate allele */
    private StringProperty alternateAllele = new SimpleStringProperty(this, "alternateAllele");

    /* Snippet */
    private StringProperty snippet = new SimpleStringProperty(this, "snippet");

    /* Genotype */
    private StringProperty genotype = new SimpleStringProperty(this, "genotype");

    /* Variant class */
    private StringProperty variantClass = new SimpleStringProperty(this, "variantClass");

    /* Pathomechanism */
    private StringProperty pathomechanism = new SimpleStringProperty(this, "pathomechanism");

    /* Cosegregation combobox - {yes, no} */
    private StringProperty cosegregation = new SimpleStringProperty(this, "cosegregation");

    /* Comparability combobox - {yes, no} */
    private StringProperty comparability = new SimpleStringProperty(this, "comparability");


    public String getChromosome() {
        return chromosome.get();
    }


    public void setChromosome(String newChromosome) {
        chromosome.set(newChromosome);
    }


    public StringProperty chromosomeProperty() {
        return chromosome;
    }


    public String getPosition() {
        return position.get();
    }


    public void setPosition(String newPosition) {
        position.set(newPosition);
    }


    public StringProperty positionProperty() {
        return position;
    }


    public String getReferenceAllele() {
        return referenceAllele.get();
    }


    public void setReferenceAllele(String newReference) {
        referenceAllele.set(newReference);
    }


    public StringProperty referenceAlleleProperty() {
        return referenceAllele;
    }


    public String getAlternateAllele() {
        return alternateAllele.get();
    }


    public void setAlternateAllele(String newReference) {
        alternateAllele.set(newReference);
    }


    public StringProperty alternateAlleleProperty() {
        return alternateAllele;
    }


    public String getSnippet() {
        return snippet.get();
    }


    public void setSnippet(String newSnippet) {
        snippet.set(newSnippet);
    }


    public StringProperty snippetProperty() {
        return snippet;
    }


    public String getGenotype() {
        return genotype.get();
    }


    public void setGenotype(String newGenotype) {
        genotype.set(newGenotype);
    }


    public StringProperty genotypeProperty() {
        return genotype;
    }


    public String getVariantClass() {
        return variantClass.get();
    }


    public void setVariantClass(String newVariantClass) {
        variantClass.set(newVariantClass);
    }


    public StringProperty variantClassProperty() {
        return variantClass;
    }


    public String getPathomechanism() {
        return pathomechanism.get();
    }


    public void setPathomechanism(String newPathomechanism) {
        pathomechanism.set(newPathomechanism);
    }


    public StringProperty pathomechanismProperty() {
        return pathomechanism;
    }


    public String getCosegregation() {
        return cosegregation.get();
    }


    public void setCosegregation(String newCosegregation) {
        cosegregation.set(newCosegregation);
    }


    public StringProperty cosegregationProperty() {
        return cosegregation;
    }


    public String getComparability() {
        return comparability.get();
    }


    public void setComparability(String newComparability) {
        comparability.set(newComparability);
    }


    public StringProperty comparabilityProperty() {
        return comparability;
    }


    public VariantMode getVariantMode() {
        return variantMode;
    }


    public void setVariantMode(VariantMode variantMode) {
        this.variantMode = variantMode;
    }


    @Override
    public int compareTo(Variant other) {
        if (this.equals(other)) {
            return 0;
        }

        int cmp = this.getChromosome().compareTo(other.getChromosome());
        if (cmp != 0) {
            return cmp;
        }

        int thisPos = Integer.parseInt(this.getPosition());
        int otherPos = Integer.parseInt(other.getPosition());
        cmp = thisPos - otherPos;
        if (cmp != 0) {
            return cmp;
        }

        cmp = this.getReferenceAllele().compareTo(other.getReferenceAllele());
        if (cmp != 0) {
            return cmp;
        }

        return this.getAlternateAllele().compareTo(other.getAlternateAllele());

    }


    @Override
    public int hashCode() {
        int result = variantMode != null ? variantMode.hashCode() : 0;
        result = 31 * result + (chromosome != null ? chromosome.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (referenceAllele != null ? referenceAllele.hashCode() : 0);
        result = 31 * result + (alternateAllele != null ? alternateAllele.hashCode() : 0);
        result = 31 * result + (snippet != null ? snippet.hashCode() : 0);
        result = 31 * result + (genotype != null ? genotype.hashCode() : 0);
        result = 31 * result + (variantClass != null ? variantClass.hashCode() : 0);
        result = 31 * result + (pathomechanism != null ? pathomechanism.hashCode() : 0);
        result = 31 * result + (cosegregation != null ? cosegregation.hashCode() : 0);
        result = 31 * result + (comparability != null ? comparability.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Variant)) {
            return false;
        }
        Variant otherVariant = (Variant) other;
        return this.hashCode() == otherVariant.hashCode();
    }


    @Override
    public String toString() {
        return String.format("chr%s:%s%s>%s", getChromosome(), getPosition(),
                getReferenceAllele(), getAlternateAllele());
    }
}
