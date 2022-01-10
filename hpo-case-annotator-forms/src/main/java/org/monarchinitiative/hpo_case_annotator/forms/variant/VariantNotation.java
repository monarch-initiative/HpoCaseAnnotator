package org.monarchinitiative.hpo_case_annotator.forms.variant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public enum VariantNotation {

    SEQUENCE("Sequence variant"),

    SYMBOLIC("Symbolic variant"),
    BREAKEND("Breakend variant");

    private static final Logger LOGGER = LoggerFactory.getLogger(VariantNotation.class);
    private final String label;

    VariantNotation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static Optional<VariantNotation> fromLabel(String label) {
        switch (label) {
            case "Sequence variant":
                return Optional.of(SEQUENCE);
            case "Symbolic variant":
                return Optional.of(SYMBOLIC);
            case "Breakend variant":
                return Optional.of(BREAKEND);
            default:
                LOGGER.warn("Unknown label `{}`", label);
                return Optional.empty();
        }
    }
}
