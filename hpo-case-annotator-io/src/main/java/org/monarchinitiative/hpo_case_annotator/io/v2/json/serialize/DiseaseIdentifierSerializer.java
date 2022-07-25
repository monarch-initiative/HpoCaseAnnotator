package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;

import java.io.IOException;

public class DiseaseIdentifierSerializer extends StdSerializer<DiseaseIdentifier> {

    public DiseaseIdentifierSerializer() {
        this(DiseaseIdentifier.class);
    }

    public DiseaseIdentifierSerializer(Class<DiseaseIdentifier> t) {
        super(t);
    }

    @Override
    public void serialize(DiseaseIdentifier diseaseIdentifier, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("diseaseId", diseaseIdentifier.id().getValue());
        gen.writeStringField("diseaseName", diseaseIdentifier.getDiseaseName());

        gen.writeEndObject();
    }
}
