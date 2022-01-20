package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;

import java.io.IOException;

public class DiseaseStatusDeserializer extends StdDeserializer<DiseaseStatus> {

    public DiseaseStatusDeserializer() {
        this(DiseaseStatus.class);
    }

    protected DiseaseStatusDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DiseaseStatus deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        DiseaseIdentifier diseaseIdentifier = codec.treeToValue(node.get("diseaseIdentifier"), DiseaseIdentifier.class);
        boolean isExcluded = node.get("isExcluded").asBoolean();

        return DiseaseStatus.of(diseaseIdentifier, isExcluded);
    }
}
