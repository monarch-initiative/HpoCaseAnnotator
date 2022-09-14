package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.GestationalAge;

import java.io.IOException;

import static org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize.Util.writeNullableNumber;

public class GestationalAgeSerializer extends StdSerializer<GestationalAge> {

    public GestationalAgeSerializer() {
        this(GestationalAge.class);
    }

    public GestationalAgeSerializer(Class<GestationalAge> t) {
        super(t);
    }

    @Override
    public void serialize(GestationalAge value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeNullableNumber(gen, "weeks", value.getWeeks());
        writeNullableNumber(gen, "days", value.getDays());

        gen.writeEndObject();
    }
}
