package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.util.List;

public interface StudyMetadata {

    static StudyMetadata of(String freeText, EditHistory createdBy, List<EditHistory> modifiedBy) {
        return new StudyMetadataDefault(freeText, createdBy, List.copyOf(modifiedBy));
    }

    /**
     * @return String with free text written down by the curator.
     */
    String freeText();

    /**
     * @return timestamp, software version, and curator id of the first author of the study.
     */
    EditHistory createdBy();

    /**
     * @return track record with timestamp, software version, and curator id of the successive edits of the study.
     */
    List<EditHistory> modifiedBy();

    int hashCode();

    boolean equals(Object o);
}