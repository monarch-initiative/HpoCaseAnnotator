package org.monarchinitiative.hpo_case_annotator.model.v2;

record VitalStatusDefault(Status status, TimeElement timeOfDeath) implements VitalStatus {
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public TimeElement getTimeOfDeath() {
        return timeOfDeath;
    }
}
