package org.monarchinitiative.hpo_case_annotator.observable.v2;

/**
 * Variant coordinates are stored in one of these notations within {@link ObservableCuratedVariant}.
 */
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
