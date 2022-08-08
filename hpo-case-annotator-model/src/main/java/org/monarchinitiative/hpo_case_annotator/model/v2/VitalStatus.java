package org.monarchinitiative.hpo_case_annotator.model.v2;

public interface VitalStatus {

    static VitalStatus of(Status status, TimeElement timeOfDeath) {
        return new VitalStatusDefault(status, timeOfDeath);
    }

    enum Status {
        UNKNOWN,
        ALIVE,
        DECEASED
    }

    Status getStatus();

    TimeElement getTimeOfDeath();

}
