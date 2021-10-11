package org.monarchinitiative.hpo_case_annotator.io.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;

import java.io.IOException;
import java.time.Instant;

public class EditHistoryDeserializer extends StdDeserializer<EditHistory> {

    public EditHistoryDeserializer() {
        this(EditHistory.class);
    }

    protected EditHistoryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public EditHistory deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String curatorId = node.get("curatorId").asText();
        String softwareVersion = node.get("softwareVersion").asText();
        Instant timestamp = Instant.parse(node.get("timestamp").asText());

        return EditHistory.of(curatorId, softwareVersion, timestamp);
    }
}
