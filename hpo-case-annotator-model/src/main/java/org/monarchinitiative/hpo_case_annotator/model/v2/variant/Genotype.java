package org.monarchinitiative.hpo_case_annotator.model.v2.variant;

public enum Genotype {

    UNKNOWN("?", "Unknown"),
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

}
