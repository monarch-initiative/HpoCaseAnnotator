package org.monarchinitiative.hpo_case_annotator.forms;

import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;

public class MetadataController implements ComponentController<StudyMetadata>{

    private StudyMetadata metadata;

    @Override
    public void presentComponent(StudyMetadata studyMetadata) {
        // TODO - implement
        this.metadata = studyMetadata;
    }

    @Override
    public StudyMetadata getComponent() throws InvalidComponentDataException {
        // TODO - implement
        return metadata;
    }
}
