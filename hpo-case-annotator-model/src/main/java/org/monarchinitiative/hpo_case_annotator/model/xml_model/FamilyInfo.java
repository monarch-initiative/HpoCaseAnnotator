package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing familial information. Currently family/patient ID, patient's sex and age are being recorded.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class FamilyInfo {

    /* What was the id of proband/index case */
    private final StringProperty familyOrPatientID = new SimpleStringProperty(this, "familyOrPatientID", "");

    /* What was his/her sex */
    private final StringProperty sex = new SimpleStringProperty(this, "sex", "");

    /* What was the age of proband */
    private final StringProperty age = new SimpleStringProperty(this, "age", "");


    public FamilyInfo(String familyOrPatientId, String sex, String age) {
        this.familyOrPatientID.set(familyOrPatientId);
        this.sex.set(sex);
        this.age.set(age);
    }


    public FamilyInfo() {
        // no-op
    }


    @Override
    public int hashCode() {
        int result = getFamilyOrPatientID() != null ? getFamilyOrPatientID().hashCode() : 0;
        result = 31 * result + (getSex() != null ? getSex().hashCode() : 0);
        result = 31 * result + (getAge() != null ? getAge().hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FamilyInfo that = (FamilyInfo) o;

        if (getFamilyOrPatientID() != null ? !getFamilyOrPatientID().equals(that.getFamilyOrPatientID()) : that.getFamilyOrPatientID() != null)
            return false;
        if (getSex() != null ? !getSex().equals(that.getSex()) : that.getSex() != null) return false;
        return getAge() != null ? getAge().equals(that.getAge()) : that.getAge() == null;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FamilyInfo{");
        sb.append("familyOrPatientID=").append(familyOrPatientID.get());
        sb.append(", sex=").append(sex.get());
        sb.append(", age=").append(age.get());
        sb.append('}');
        return sb.toString();
    }


    public String getFamilyOrPatientID() {
        return familyOrPatientID.get();
    }


    public void setFamilyOrPatientID(String newFamilyOrPatientID) {
        familyOrPatientID.set(newFamilyOrPatientID);
    }


    public StringProperty familyOrPatientIDProperty() {
        return familyOrPatientID;
    }


    public String getSex() {
        return sex.get();
    }


    public void setSex(String value) {
        sex.set(value);
    }


    public StringProperty sexProperty() {
        return sex;
    }


    public String getAge() {
        return age.get();
    }


    public void setAge(String value) {
        age.set(value);
    }


    public StringProperty ageProperty() {
        return age;
    }
}
