package org.monarchinitiative.hpo_case_annotator.io.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;

import java.io.IOException;
import java.time.Period;

public class AgeRangeDeserializer extends StdDeserializer<AgeRange> {

    public AgeRangeDeserializer() {
        this(AgeRange.class);
    }

    public AgeRangeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AgeRange deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Period startAge = Period.parse(node.get("startAge").asText());
        Period endAge = null;
        if (node.has("endAge"))
            endAge = Period.parse(node.get("endAge").asText());

        return (endAge == null)
                ? AgeRange.point(startAge)
                : AgeRange.of(startAge, endAge);
    }

}
