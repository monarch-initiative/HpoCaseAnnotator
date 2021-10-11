package org.monarchinitiative.hpo_case_annotator.io.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;

import java.io.IOException;

public class PhenotypicFeatureSerializer extends StdSerializer<PhenotypicFeature> {

    public PhenotypicFeatureSerializer() {
        this(PhenotypicFeature.class);
    }

    protected PhenotypicFeatureSerializer(Class<PhenotypicFeature> t) {
        super(t);
    }

    @Override
    public void serialize(PhenotypicFeature value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("termId", value.termId().getValue());
        gen.writeBooleanField("isExcluded", value.isExcluded());

        gen.writeEndObject();
    }
}
