package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;

import java.io.IOException;

import static org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.Util.readNullableInteger;

public class AgeDeserializer extends StdDeserializer<Age> {

    public AgeDeserializer() {
        this(Age.class);
    }

    public AgeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Age deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        return Age.of(readNullableInteger(node, "years"), readNullableInteger(node, "months"),  readNullableInteger(node, "days"));
    }

}
