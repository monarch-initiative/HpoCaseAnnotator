package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;

import java.io.IOException;

public class DiseaseStatusSerializer extends StdSerializer<DiseaseStatus> {

    public DiseaseStatusSerializer() {
        this(DiseaseStatus.class);
    }

    public DiseaseStatusSerializer(Class<DiseaseStatus> t) {
        super(t);
    }

    @Override
    public void serialize(DiseaseStatus diseaseStatus, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("diseaseIdentifier", diseaseStatus.diseaseId());
        gen.writeBooleanField("isExcluded", diseaseStatus.isExcluded());

        gen.writeEndObject();
    }
}
