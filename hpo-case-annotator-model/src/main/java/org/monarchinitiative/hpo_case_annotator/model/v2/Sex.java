package org.monarchinitiative.hpo_case_annotator.model.v2;

public enum Sex {
    UNKNOWN("Unknown"),
    MALE("Male"),
    FEMALE("Female");

    private final String label;

    Sex(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
