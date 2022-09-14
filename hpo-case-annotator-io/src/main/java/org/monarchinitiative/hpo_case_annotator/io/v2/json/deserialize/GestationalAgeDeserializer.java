package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;

import java.io.IOException;

import static org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.Util.readNullableInteger;

public class GestationalAgeDeserializer extends StdDeserializer<GestationalAge> {

    public GestationalAgeDeserializer() {
        this(GestationalAge.class);
    }

    public GestationalAgeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GestationalAge deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return GestationalAge.of(readNullableInteger(node, "weeks"), readNullableInteger(node, "days"));
    }
}
