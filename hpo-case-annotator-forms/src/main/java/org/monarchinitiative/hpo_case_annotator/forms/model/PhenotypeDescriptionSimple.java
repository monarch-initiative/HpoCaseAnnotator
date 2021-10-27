package org.monarchinitiative.hpo_case_annotator.forms.model;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.Period;
import java.util.Objects;

public class PhenotypeDescriptionSimple implements PhenotypeDescription {

    private TermId termId;
    private String label;
    private Period onset, resolution;
    private boolean present;

    public static PhenotypeDescription of(TermId termId, String label, Period onset, Period resolution, boolean present) {
        return new PhenotypeDescriptionSimple(termId, label, onset, resolution, present);
    }

    private PhenotypeDescriptionSimple(TermId termId, String label, Period onset, Period resolution, boolean present) {
        this.termId = termId;
        this.label = label;
        this.onset = onset;
        this.resolution = resolution;
        this.present = present;
    }

    @Override
    public TermId getTermId() {
        return termId;
    }

    public void setTermId(TermId termId) {
        this.termId = termId;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public Period getOnset() {
        return onset;
    }

    public void setOnset(Period onset) {
        this.onset = onset;
    }

    @Override
    public Period getResolution() {
        return resolution;
    }

    public void setResolution(Period resolution) {
        this.resolution = resolution;
    }

    @Override
    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhenotypeDescriptionSimple that = (PhenotypeDescriptionSimple) o;
        return present == that.present && Objects.equals(termId, that.termId) && Objects.equals(label, that.label) && Objects.equals(onset, that.onset) && Objects.equals(resolution, that.resolution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termId, label, onset, resolution, present);
    }

    @Override
    public String toString() {
        return "SimplePhenotypicFeature{" +
                "termId=" + termId +
                ", label='" + label + '\'' +
                ", onset=" + onset +
                ", resolution=" + resolution +
                ", present=" + present +
                '}';
    }
}
