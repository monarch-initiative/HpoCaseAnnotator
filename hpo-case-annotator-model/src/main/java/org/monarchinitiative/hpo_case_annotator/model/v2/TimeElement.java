package org.monarchinitiative.hpo_case_annotator.model.v2;

/**
 * Naive specification of oneof field of Phenopacket Schema.
 * <p>
 * <b>Important:</b> {@link #getTimeElementCase()} must <b>not</b> be {@code null}.
 */
public interface TimeElement {

    static TimeElement gestationalAge(GestationalAge gestationalAge) {
        return of(TimeElementCase.GESTATIONAL_AGE, gestationalAge, null, null, null);
    }

    static TimeElement age(Age age) {
        return of(TimeElementCase.AGE, null, age, null, null);
    }

    static TimeElement ageRange(AgeRange ageRange) {
        return of(TimeElementCase.AGE_RANGE, null, null, ageRange, null);
    }

    static TimeElement of(
            TimeElementCase timeElementCase,
            GestationalAge gestationalAge,
            Age age,
            AgeRange ageRange,
            OntologyClass ontologyClass) {
        return new TimeElementDefault(timeElementCase, gestationalAge, age, ageRange, ontologyClass);
    }

    enum TimeElementCase {
        GESTATIONAL_AGE,
        AGE,
        AGE_RANGE,
        ONTOLOGY_CLASS
    }

    TimeElementCase getTimeElementCase();

    /**
     * @return gestational age or {@code null} if {@link #getTimeElementCase()} is not set to {@link TimeElementCase#GESTATIONAL_AGE}.
     */
    GestationalAge getGestationalAge();

    /**
     * @return age or {@code null} if {@link #getTimeElementCase()} is not set to {@link TimeElementCase#AGE}.
     */
    Age getAge();

    /**
     * @return age range or {@code null} if {@link #getTimeElementCase()} is not set to {@link TimeElementCase#AGE_RANGE}.
     */
    AgeRange getAgeRange();

    /**
     * @return {@link OntologyClass} or {@code null} if {@link #getTimeElementCase()} is not set to {@link TimeElementCase#ONTOLOGY_CLASS}.
     */
    OntologyClass getOntologyClass();

}
