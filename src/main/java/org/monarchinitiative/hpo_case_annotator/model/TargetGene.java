package org.monarchinitiative.hpo_case_annotator.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing attributes of gene in which a mutation was observed. Currently gene name (symbol), entrezID and
 * chromosome are being recorded.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class TargetGene {

    /* Gene name property */
    private final StringProperty geneName = new SimpleStringProperty(this, "geneName", "");

    /* Entrez ID */
    private final StringProperty entrezID = new SimpleStringProperty(this, "entrezID", "");

    /* Chromosome on which the gene is localised */
    private final StringProperty chromosome = new SimpleStringProperty(this, "chromosome", "");


    public TargetGene() {
        // no-op
    }


    public TargetGene(String geneName, String entrezID, String chromosome) {
        this.geneName.set(geneName);
        this.entrezID.set(entrezID);
        this.chromosome.set(chromosome);
    }


    @Override
    public int hashCode() {
        int result = getGeneName() != null ? getGeneName().hashCode() : 0;
        result = 31 * result + (getEntrezID() != null ? getEntrezID().hashCode() : 0);
        result = 31 * result + (getChromosome() != null ? getChromosome().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetGene that = (TargetGene) o;

        if (getGeneName() != null ? !getGeneName().equals(that.getGeneName()) : that.getGeneName() != null)
            return false;
        if (getEntrezID() != null ? !getEntrezID().equals(that.getEntrezID()) : that.getEntrezID() != null)
            return false;
        return getChromosome() != null ? getChromosome().equals(that.getChromosome()) : that.getChromosome() == null;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TargetGene{");
        sb.append("geneName=").append(geneName.get());
        sb.append(", entrezID=").append(entrezID.get());
        sb.append(", chromosome=").append(chromosome.get());
        sb.append('}');
        return sb.toString();
    }


    @JsonGetter
    public String getGeneName() {
        return geneName.get();
    }


    @JsonSetter
    public void setGeneName(String newGeneName) {
        geneName.set(newGeneName);
    }


    public StringProperty geneNameProperty() {
        return geneName;
    }


    @JsonGetter
    public String getEntrezID() {
        return entrezID.get();
    }


    @JsonSetter
    public void setEntrezID(String newEntrezID) {
        entrezID.set(newEntrezID);
    }


    public StringProperty entrezIDProperty() {
        return entrezID;
    }


    @JsonGetter
    public String getChromosome() {
        return chromosome.get();
    }


    @JsonSetter
    public void setChromosome(String newChromosome) {
        chromosome.set(newChromosome);
    }


    public StringProperty chromosomeProperty() {
        return chromosome;
    }
}
