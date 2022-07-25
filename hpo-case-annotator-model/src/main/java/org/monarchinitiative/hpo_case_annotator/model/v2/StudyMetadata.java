package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

public interface StudyMetadata {

    static StudyMetadata of(String freeText, EditHistory createdBy, List<? extends EditHistory> modifiedBy) {
        return new StudyMetadataDefault(freeText, createdBy, List.copyOf(modifiedBy));
    }

    /**
     * @return String with free text written down by the curator.
     */
    String getFreeText();

    /**
     * @return timestamp, software version, and curator id of the first author of the study.
     */
    EditHistory getCreatedBy();

    /**
     * @return track record with timestamp, software version, and curator id of the successive edits of the study.
     */
    List<? extends EditHistory> getModifiedBy();

    int hashCode();

    boolean equals(Object o);
}
