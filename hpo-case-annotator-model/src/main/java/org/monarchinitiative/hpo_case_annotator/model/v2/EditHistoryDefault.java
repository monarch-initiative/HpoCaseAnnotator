package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Instant;

record EditHistoryDefault(String curatorId, String softwareVersion, Instant timestamp) implements EditHistory {
    @Override
    public String getCuratorId() {
        return curatorId;
    }

    @Override
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
