package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.svart.ConfidenceInterval;

import java.io.IOException;

public class ConfidenceIntervalSerializer extends StdSerializer<ConfidenceInterval> {

    public ConfidenceIntervalSerializer() {
        this(ConfidenceInterval.class);
    }

    protected ConfidenceIntervalSerializer(Class<ConfidenceInterval> t) {
        super(t);
    }

    @Override
    public void serialize(ConfidenceInterval confidenceInterval, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("lowerBound", confidenceInterval.lowerBound());
        gen.writeNumberField("upperBound", confidenceInterval.upperBound());

        gen.writeEndObject();
    }
}
