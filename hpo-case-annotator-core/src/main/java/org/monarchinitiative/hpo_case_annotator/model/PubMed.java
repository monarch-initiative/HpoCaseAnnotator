package org.monarchinitiative.hpo_case_annotator.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PubMed {


    private StringProperty pubMedText = new SimpleStringProperty(this, "pubMedText");


    public PubMed() {
    }


    public PubMed(String pubMedText) {
        this.pubMedText.set(pubMedText);
    }


    @JsonGetter
    public String getPubMedText() {
        return pubMedText.get();
    }


    @JsonSetter
    public void setPubMedText(String newPubMedText) {
        pubMedText.set(newPubMedText);
    }


    public StringProperty pubMedTextProperty() {
        return pubMedText;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PubMed{");
        sb.append("pubMedText=").append(pubMedText.get());
        sb.append('}');
        return sb.toString();
    }
}
