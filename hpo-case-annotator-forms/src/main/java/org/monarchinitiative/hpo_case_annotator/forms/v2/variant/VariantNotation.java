package org.monarchinitiative.hpo_case_annotator.forms.v2.variant;

public enum VariantNotation {

    SEQUENCE("Sequence variant"),

    SYMBOLIC("Symbolic variant"),
    BREAKEND("Breakend variant");

    private final String label;

    VariantNotation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

}
