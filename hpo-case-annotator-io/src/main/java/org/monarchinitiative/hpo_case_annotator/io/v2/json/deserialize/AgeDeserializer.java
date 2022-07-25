package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;

import java.io.IOException;
import java.time.Period;

public class AgeDeserializer extends StdDeserializer<Age> {

    public AgeDeserializer() {
        this(Age.class);
    }

    public AgeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Age deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        boolean isGestational = node.get("isGestational").asBoolean();
        int years = node.get("years").asInt();
        int months = node.get("months").asInt();
        int weeks = node.get("weeks").asInt();
        int days = node.get("days").asInt();

        return Age.of(isGestational, years, months, weeks, days);
    }


    public static Age parseAge(ObjectCodec codec, JsonNode node) throws JsonProcessingException {
        if (node.isTextual()) {
            Period period = Period.parse(node.asText());
            return Age.ofYearsMonthsDays(period.getYears(), period.getMonths(), period.getDays());
        } else {
            return codec.treeToValue(node, Age.class);
        }
    }

}
