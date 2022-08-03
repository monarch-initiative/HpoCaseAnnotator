package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;

import java.io.IOException;

import static org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize.Util.writeNullableNumber;

public class AgeSerializer extends StdSerializer<Age> {

    public AgeSerializer() {
        this(Age.class);
    }

    public AgeSerializer(Class<Age> t) {
        super(t);
    }

    @Override
    public void serialize(Age age, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();

        writeNullableNumber(gen, "years", age.getYears());
        writeNullableNumber(gen, "months", age.getMonths());
        writeNullableNumber(gen, "days", age.getDays());

        gen.writeEndObject();
    }
}
