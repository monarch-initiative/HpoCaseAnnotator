package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class DiseaseIdentifierDeserializer extends StdDeserializer<DiseaseIdentifier> {


    public DiseaseIdentifierDeserializer() {
        this(DiseaseIdentifier.class);
    }

    protected DiseaseIdentifierDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DiseaseIdentifier deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        TermId diseaseId = TermId.of(node.get("diseaseId").asText());
        String diseaseName = node.get("diseaseName").asText();

        return DiseaseIdentifier.of(diseaseId, diseaseName);
    }
}
