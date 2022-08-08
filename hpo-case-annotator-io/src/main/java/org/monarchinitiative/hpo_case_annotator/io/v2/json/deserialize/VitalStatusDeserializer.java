package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.hpo_case_annotator.model.v2.VitalStatus;

import java.io.IOException;

public class VitalStatusDeserializer extends StdDeserializer<VitalStatus> {

    public VitalStatusDeserializer() {
        this(VitalStatus.class);
    }

    public VitalStatusDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VitalStatus deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        VitalStatus.Status status = VitalStatus.Status.valueOf(node.get("status").asText());
        TimeElement timeOfDeath = codec.treeToValue(node.get("timeOfDeath"), TimeElement.class);

        return VitalStatus.of(status, timeOfDeath);
    }
}
