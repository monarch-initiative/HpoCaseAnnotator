package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;

import java.io.IOException;

public class AgeRangeSerializer extends StdSerializer<AgeRange> {

    public AgeRangeSerializer() {
        this(AgeRange.class);
    }

    public AgeRangeSerializer(Class<AgeRange> t) {
        super(t);
    }

    @Override
    public void serialize(AgeRange ageRange, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();

        gen.writeObjectField("onset", ageRange.getOnset());
        gen.writeObjectField("resolution", ageRange.getResolution());

        gen.writeEndObject();
    }
}
