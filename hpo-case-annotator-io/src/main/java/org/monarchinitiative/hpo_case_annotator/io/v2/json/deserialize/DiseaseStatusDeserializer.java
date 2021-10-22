package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.phenol.ontology.data.TermId;

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
        JsonNode node = jp.getCodec().readTree(jp);

        TermId diseaseId = TermId.of(node.get("diseaseId").asText());
        String diseaseName = node.get("diseaseName").asText();
        boolean isExcluded = node.get("isExcluded").asBoolean();

        return DiseaseStatus.of(diseaseId, diseaseName, isExcluded);
    }
}
