package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StudyMetadataDeserializer extends StdDeserializer<StudyMetadata> {

    public StudyMetadataDeserializer() {
        this(StudyMetadata.class);
    }

    protected StudyMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StudyMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        JsonNode freeTextNode = node.get("freeText");
        String freeText = freeTextNode == null || freeTextNode.isNull() ? null : freeTextNode.asText();
        EditHistory createdBy = codec.treeToValue(node.get("createdBy"), EditHistory.class);


        Iterator<JsonNode> iterator = node.get("modifiedBy").elements();
        List<EditHistory> modifiedBy = new LinkedList<>();
        while (iterator.hasNext()) {
            EditHistory editHistory = codec.treeToValue(iterator.next(), EditHistory.class);
            modifiedBy.add(editHistory);
        }

        return StudyMetadata.of(freeText, createdBy, modifiedBy);
    }
}
