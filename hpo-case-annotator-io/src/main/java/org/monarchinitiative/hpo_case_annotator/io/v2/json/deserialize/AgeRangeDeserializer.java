package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Period;

public class AgeRangeDeserializer extends StdDeserializer<AgeRange> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgeRangeDeserializer.class);

    public AgeRangeDeserializer() {
        this(AgeRange.class);
    }

    public AgeRangeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AgeRange deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        // First, read the current version where Age is a first class citizen of the HCA data model.
        try {
            Age start = codec.treeToValue(node.get("start"), Age.class);
            Age end = codec.treeToValue(node.get("end"), Age.class);
            return AgeRange.of(start, end);
        } catch (Exception e) {
            // Alternatively, try to parse the raw periods (obsolete).
            try {
                Period start = Period.parse(node.get("onset").asText());
                Period end = null;
                if (node.has("resolution")) {
                    end = Period.parse(node.get("resolution").asText());
                }

                AgeRange range = end == null
                        ? AgeRange.point(Age.ofYearsMonthsDays(start.getYears(), start.getMonths(), start.getDays()))
                        : AgeRange.of(Age.ofYearsMonthsDays(start.getYears(), start.getMonths(), start.getDays()), Age.ofYearsMonthsDays(end.getYears(), end.getMonths(), end.getDays()));
                LOGGER.warn("Decoded obsolete age model. Convert the data into the new model ASAP.");
                return range;
            } catch (Exception ex) {
                throw new JsonParseException(jp, "Unable to parse age range");
            }
        }
    }

}
