package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.io.IOException;

public class PedigreeMemberDeserializer extends StdDeserializer<PedigreeMember> {

    public PedigreeMemberDeserializer() {
        this(PedigreeMember.class);
    }

    protected PedigreeMemberDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PedigreeMember deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        JsonNode paternalIdNode = node.get("paternalId");
        String paternalId = (paternalIdNode.isTextual()) ? paternalIdNode.asText() : null;

        JsonNode maternalIdNode = node.get("maternalId");
        String maternalId = (maternalIdNode.isTextual()) ? maternalIdNode.asText() : null;

        boolean isProband = node.get("isProband").asBoolean();

        // to cut the boilerplate
        Individual individual = codec.treeToValue(node, Individual.class);

        return PedigreeMember.of(individual.getId(),
                paternalId,
                maternalId,
                isProband,
                individual.getPhenotypicFeatures(),
                individual.getDiseaseStates(),
                individual.getGenotypes(),
                individual.getAge() == null ? null : individual.getAge(),
                individual.getSex());
    }
}
