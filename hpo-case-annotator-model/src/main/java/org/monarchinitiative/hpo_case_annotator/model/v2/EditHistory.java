package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Instant;

public interface EditHistory {

    static EditHistory of(String curatorId, String softwareVersion, Instant timestamp) {
        return new EditHistoryDefault(curatorId, softwareVersion, timestamp);
    }

    String getCuratorId();

    /**
     * @return version of the curation software (e.g. {@code 1.0.2}).
     */
    String getSoftwareVersion();

    Instant getTimestamp();

}
