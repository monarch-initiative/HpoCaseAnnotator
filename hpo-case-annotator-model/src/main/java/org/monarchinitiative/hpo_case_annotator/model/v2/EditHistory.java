package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Instant;

public interface EditHistory {

    static EditHistory of(String curatorId, String softwareVersion, Instant timestamp) {
        return new EditHistoryDefault(curatorId, softwareVersion, timestamp);
    }

    String curatorId();

    String softwareVersion();

    Instant timestamp();

}
