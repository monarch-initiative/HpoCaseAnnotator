package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Disease;

import java.io.IOException;

public class DiseaseSerializer extends StdSerializer<Disease> {

    public DiseaseSerializer() {
        this(Disease.class);
    }

    public DiseaseSerializer(Class<Disease> t) {
        super(t);
    }

    @Override
    public void serialize(Disease disease, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("diseaseId", disease.diseaseId().getValue());
        gen.writeStringField("diseaseName", disease.diseaseName());
        gen.writeBooleanField("isExcluded", disease.isExcluded());

        gen.writeEndObject();
    }
}
