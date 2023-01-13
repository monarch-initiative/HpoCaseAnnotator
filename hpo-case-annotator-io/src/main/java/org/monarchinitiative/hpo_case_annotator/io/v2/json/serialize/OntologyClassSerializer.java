package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.OntologyClass;

import java.io.IOException;

public class OntologyClassSerializer extends StdSerializer<OntologyClass> {

    public OntologyClassSerializer() {
        this(OntologyClass.class);
    }

    protected OntologyClassSerializer(Class<OntologyClass> t) {
        super(t);
    }

    @Override
    public void serialize(OntologyClass value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.getId().getValue());
        gen.writeStringField("label", value.getLabel());
        gen.writeEndObject();
    }
}
