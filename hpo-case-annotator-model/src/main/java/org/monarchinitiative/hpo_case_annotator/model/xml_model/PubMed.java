package org.monarchinitiative.hpo_case_annotator.model.xml_model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PubMed {


    private StringProperty pubMedText = new SimpleStringProperty(this, "pubMedText");


    public PubMed() {
    }


    public PubMed(String pubMedText) {
        this.pubMedText.set(pubMedText);
    }


    public String getPubMedText() {
        return pubMedText.get();
    }


    public void setPubMedText(String newPubMedText) {
        pubMedText.set(newPubMedText);
    }


    public StringProperty pubMedTextProperty() {
        return pubMedText;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubMed{");
        sb.append("pubMedText=").append(pubMedText.get());
        sb.append('}');
        return sb.toString();
    }
}
