package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean defining attributes of HPO term. Currently hpoId, label (hpoName) and whether the term was observed/unobserved
 * in proband are being recorded.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class HPO {

    /* HPO id string */
    private StringProperty hpoId = new SimpleStringProperty(this, "hpoId");

    /* HPO term name */
    private StringProperty hpoName = new SimpleStringProperty(this, "hpoName");

    /* Was term observed or not? Is it "YES" or "NOT" term? Term is implicitly observerd. */
    private StringProperty observed = new SimpleStringProperty(this, "observed");


    public HPO() {
        // no-op
    }


    public HPO(
            String hpoId,
            String hpoName,
            String observed) {
        this.hpoId.set(hpoId);
        this.hpoName.set(hpoName);
        this.observed.set(observed);
    }


    public String getHpoId() {
        return hpoId.get();
    }


    public void setHpoId(String newHpoId) {
        hpoId.set(newHpoId);
    }


    public StringProperty hpoIdProperty() {
        return hpoId;
    }


    public String getHpoName() {
        return hpoName.get();
    }


    public void setHpoName(String newHpoName) {
        hpoName.set(newHpoName);
    }


    public StringProperty hpoNameProperty() {
        return hpoName;
    }


    public String getObserved() {
        return observed.get();
    }


    public void setObserved(String observed) {
        this.observed.set(observed);
    }


    public StringProperty observedProperty() {
        return observed;
    }


    @Override
    public int hashCode() {
        int result = getHpoId() != null ? getHpoId().hashCode() : 0;
        result = 31 * result + (getObserved() != null ? getObserved().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HPO hpo = (HPO) o;

        if (getHpoId() != null ? !getHpoId().equals(hpo.getHpoId()) : hpo.getHpoId() != null) return false;
        return getObserved() != null ? getObserved().equals(hpo.getObserved()) : hpo.getObserved() == null;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HPO{");
        sb.append("hpoId=").append(hpoId.get());
        sb.append(", hpoName=").append(hpoName.get());
        sb.append(", observed=").append(observed.get());
        sb.append('}');
        return sb.toString();
    }
}
