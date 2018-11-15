package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing Biocurator details. Currently only biocurator ID, but we're open to also store e.g name & e-mail
 * later.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Biocurator {

    /* Field to store biocurator ID */
    private StringProperty bioCuratorId = new SimpleStringProperty(this, "bioCuratorId");


    public Biocurator() {
        // no-op
    }


    public Biocurator(String bioCuratorId) {
        this.bioCuratorId.set(bioCuratorId);
    }


    @Override
    public int hashCode() {
        return getBioCuratorId() != null ? getBioCuratorId().hashCode() : 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Biocurator that = (Biocurator) o;

        return getBioCuratorId() != null ? getBioCuratorId().equals(that.getBioCuratorId()) : that.getBioCuratorId() == null;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Biocurator{");
        sb.append("bioCuratorId=").append(bioCuratorId.get());
        sb.append('}');
        return sb.toString();
    }


    public final String getBioCuratorId() {
        return bioCuratorId.get();
    }


    public final void setBioCuratorId(String newBioCuratorId) {
        bioCuratorId.set(newBioCuratorId);
    }


    public StringProperty bioCuratorIdProperty() {
        return bioCuratorId;
    }
}
