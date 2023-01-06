package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;

import java.io.IOException;

public class StudyMetadataSerializer extends StdSerializer<StudyMetadata> {

    public StudyMetadataSerializer() {
        this(StudyMetadata.class);
    }

    public StudyMetadataSerializer(Class<StudyMetadata> t) {
        super(t);
    }

    @Override
    public void serialize(StudyMetadata studyMetadata, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("freeText", studyMetadata.getFreeText());

        gen.writeObjectField("createdBy", studyMetadata.getCreatedBy());

        gen.writeArrayFieldStart("modifiedBy");
        for (EditHistory modified : studyMetadata.getModifiedBy())
            gen.writeObject(modified);
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
