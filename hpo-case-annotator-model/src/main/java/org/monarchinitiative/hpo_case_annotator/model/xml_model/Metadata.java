package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing Disease details. Currently disease database, disease ID and disease name is being recorded.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Metadata {

    private final StringProperty metadataText = new SimpleStringProperty(this, "metadataText", "");


    public Metadata(String metadataText) {
        this.metadataText.set(metadataText);
    }


    public Metadata() {
        // no-op
    }


    @Override
    public int hashCode() {
        return getMetadataText() != null ? getMetadataText().hashCode() : 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        return getMetadataText() != null ? getMetadataText().equals(metadata.getMetadataText()) : metadata.getMetadataText() == null;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Metadata{");
        sb.append("metadataText=").append(metadataText.get());
        sb.append('}');
        return sb.toString();
    }


    public String getMetadataText() {
        return metadataText.get();
    }


    public void setMetadataText(String newMetadataText) {
        metadataText.set(newMetadataText);
    }


    public StringProperty metadataTextProperty() {
        return metadataText;
    }
}
