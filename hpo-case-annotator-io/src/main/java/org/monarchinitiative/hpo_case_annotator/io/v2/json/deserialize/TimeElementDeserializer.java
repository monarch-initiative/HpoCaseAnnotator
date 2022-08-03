package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class TimeElementDeserializer extends StdDeserializer<TimeElement> {


    public TimeElementDeserializer() {
        this(TimeElement.class);
    }

    protected TimeElementDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public TimeElement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        TimeElement.TimeElementCase teCase = TimeElement.TimeElementCase.valueOf(node.get("timeCase").asText());

        return switch (teCase) {
            case GESTATIONAL_AGE -> {
                GestationalAge gestationalAge = codec.treeToValue(node.get("gestationalAge"), GestationalAge.class);
                yield TimeElement.of(teCase, gestationalAge, null, null, null);
            }
            case AGE -> {
                Age age = codec.treeToValue(node.get("age"), Age.class);
                yield TimeElement.of(teCase, null, age, null, null);
            }
            case AGE_RANGE -> {
                AgeRange ageRange = codec.treeToValue(node.get("ageRange"), AgeRange.class);
                yield TimeElement.of(teCase, null, null, ageRange, null);
            }
            case ONTOLOGY_CLASS -> {
                TermId onsetTermId = TermId.of(node.get("ontologyClass").asText());
                yield TimeElement.of(teCase, null, null, null, onsetTermId);
            }
        };
    }

}
