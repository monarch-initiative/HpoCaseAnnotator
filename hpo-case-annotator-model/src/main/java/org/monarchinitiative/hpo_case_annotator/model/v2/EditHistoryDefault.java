package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Instant;

record EditHistoryDefault(String curatorId, String softwareVersion, Instant timestamp) implements EditHistory {
}
