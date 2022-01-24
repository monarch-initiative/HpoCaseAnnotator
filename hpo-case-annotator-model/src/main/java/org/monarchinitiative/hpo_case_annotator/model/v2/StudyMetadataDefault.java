package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

record StudyMetadataDefault(String freeText, EditHistory createdBy,
                            List<EditHistory> modifiedBy) implements StudyMetadata {
}
