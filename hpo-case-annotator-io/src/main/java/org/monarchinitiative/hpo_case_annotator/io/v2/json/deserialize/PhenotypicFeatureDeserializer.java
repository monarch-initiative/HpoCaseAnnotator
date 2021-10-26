package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class PhenotypicFeatureDeserializer extends StdDeserializer<PhenotypicFeature> {

    public PhenotypicFeatureDeserializer() {
        this(PhenotypicFeature.class);
    }

    protected PhenotypicFeatureDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PhenotypicFeature deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        TermId termId = TermId.of(node.get("termId").asText());
        boolean isExcluded = node.get("isExcluded").asBoolean();
        AgeRange observationAge = codec.treeToValue(node.get("observationAge"), AgeRange.class);

        return PhenotypicFeature.of(termId, isExcluded, observationAge);
    }
}
