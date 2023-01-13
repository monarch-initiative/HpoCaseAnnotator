package org.monarchinitiative.hpo_case_annotator.model.v2;

record TimeElementDefault(
        TimeElementCase timeElementCase,
        GestationalAge gestationalAge,
        Age age,
        AgeRange ageRange,
        OntologyClass ontologyClass) implements TimeElement {
    @Override
    public TimeElementCase getTimeElementCase() {
        return timeElementCase;
    }

    @Override
    public GestationalAge getGestationalAge() {
        return gestationalAge;
    }

    @Override
    public Age getAge() {
        return age;
    }

    @Override
    public AgeRange getAgeRange() {
        return ageRange;
    }

    @Override
    public OntologyClass getOntologyClass() {
        return ontologyClass;
    }
}
