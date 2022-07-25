package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Age;

import java.io.IOException;

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
        gen.writeBooleanField("isGestational", age.isGestational());
        gen.writeNumberField("years", age.getYears());
        gen.writeNumberField("months", age.getMonths());
        gen.writeNumberField("weeks", age.getWeeks());
        gen.writeNumberField("days", age.getDays());
        gen.writeEndObject();
    }
}
