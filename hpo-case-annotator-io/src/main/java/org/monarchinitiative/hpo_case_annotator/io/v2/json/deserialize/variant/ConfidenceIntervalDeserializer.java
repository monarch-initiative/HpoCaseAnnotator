package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.svart.ConfidenceInterval;

import java.io.IOException;

public class ConfidenceIntervalDeserializer extends StdDeserializer<ConfidenceInterval> {

    public ConfidenceIntervalDeserializer() {
        this(ConfidenceInterval.class);
    }

    protected ConfidenceIntervalDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ConfidenceInterval deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        int lowerBound = node.get("lowerBound").asInt();
        int upperBound = node.get("upperBound").asInt();

        return ConfidenceInterval.of(lowerBound, upperBound);
    }
}
