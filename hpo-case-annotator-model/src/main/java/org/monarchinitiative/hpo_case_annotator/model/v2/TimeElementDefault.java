package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.monarchinitiative.phenol.ontology.data.TermId;

record TimeElementDefault(
        TimeElementCase timeElementCase,
        GestationalAge gestationalAge,
        Age age,
        AgeRange ageRange,
        TermId ontologyClass) implements TimeElement {
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
    public TermId getOntologyClass() {
        return ontologyClass;
    }
}
