package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

public enum Genotype {

    /**
     * Indicates that the user did not set genotype. It may be known but the information was not set into the data model.
     */
    UNSET("Unset", "Unset"),
    /**
     * Incicates that the genotype has been set to unknown by the user.
     */
    UNKNOWN("Unknown", "Unknown"),
    HETEROZYGOUS("0/1", "Heterozygous"),
    HOMOZYGOUS_ALTERNATE("1/1", "Homozygous alternative"),
    HOMOZYGOUS_REFERENCE("0/0", "Homozygous reference"),
    HEMIZYGOUS("1", "Hemizygous");

    private final String code, name;

    Genotype(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isKnown() {
        return switch (this) {
            case UNSET, UNKNOWN -> false;
            case HETEROZYGOUS, HOMOZYGOUS_ALTERNATE, HOMOZYGOUS_REFERENCE, HEMIZYGOUS -> true;
        };
    }

    public boolean isUnknown() {
        return !isKnown();
    }

    public static Genotype parseCode(String value) {
        return switch (value) {
            case "Unset" -> UNSET;
            case "0/1" -> HETEROZYGOUS;
            case "1/1" -> HOMOZYGOUS_ALTERNATE;
            case "0/0" -> HOMOZYGOUS_REFERENCE;
            case "1" -> HEMIZYGOUS;
            default -> UNKNOWN;
        };
    }
}
