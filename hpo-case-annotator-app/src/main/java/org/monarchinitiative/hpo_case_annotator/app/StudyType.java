package org.monarchinitiative.hpo_case_annotator.app;

public enum StudyType {
    INDIVIDUAL("Individual study", "Study of a single individual, a case report."),
    FAMILY("Family study", "Study of 2 or more related individuals."),
    COHORT("Cohort study", "Study of 2 or more unrelated individuals.");

    private final String name;
    private final String description;

    StudyType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
