package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing Disease details. Currently disease database, disease ID and disease name are being recorded.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class Disease {

    /* Database - e.g. OMIM, NCI */
    private StringProperty database = new SimpleStringProperty(this, "database", "");

    /* Disease ID - e.g. 222100 */
    private StringProperty diseaseId = new SimpleStringProperty(this, "diseaseId", "");

    /* Disease Name - e.g. DIABETES MELLITUS, INSULIN-DEPENDENT; IDDM */
    private StringProperty diseaseName = new SimpleStringProperty(this, "diseaseName", "");


    public Disease() {
        // no-op
    }


    public Disease(String database, String diseaseId, String diseaseName) {
        this.database.set(database);
        this.diseaseId.set(diseaseId);
        this.diseaseName.set(diseaseName);
    }


    @Override
    public int hashCode() {
        int result = getDatabase() != null ? getDatabase().hashCode() : 0;
        result = 31 * result + (getDiseaseId() != null ? getDiseaseId().hashCode() : 0);
        result = 31 * result + (getDiseaseName() != null ? getDiseaseName().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disease disease = (Disease) o;

        if (getDatabase() != null ? !getDatabase().equals(disease.getDatabase()) : disease.getDatabase() != null)
            return false;
        if (getDiseaseId() != null ? !getDiseaseId().equals(disease.getDiseaseId()) : disease.getDiseaseId() != null)
            return false;
        return getDiseaseName() != null ? getDiseaseName().equals(disease.getDiseaseName()) : disease.getDiseaseName() == null;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Disease{");
        sb.append("database=").append(database.get());
        sb.append(", diseaseId=").append(diseaseId.get());
        sb.append(", diseaseName=").append(diseaseName.get());
        sb.append('}');
        return sb.toString();
    }


    public String getDatabase() {
        return database.get();
    }


    public void setDatabase(String newDatabase) {
        database.set(newDatabase);
    }


    public StringProperty databaseProperty() {
        return database;
    }


    public String getDiseaseId() {
        return diseaseId.get();
    }


    public void setDiseaseId(String newDiseaseId) {
        diseaseId.set(newDiseaseId);
    }


    public StringProperty diseaseIdProperty() {
        return diseaseId;
    }


    public String getDiseaseName() {
        return diseaseName.get();
    }


    public void setDiseaseName(String newDiseaseName) {
        diseaseName.set(newDiseaseName);
    }


    public StringProperty diseaseNameProperty() {
        return diseaseName;
    }
}
