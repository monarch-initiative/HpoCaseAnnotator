package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;

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

        gen.writeStringField("termId", value.id().getValue());
        gen.writeBooleanField("isExcluded", value.isExcluded());

        gen.writeObjectField("onset", value.getOnset());
        gen.writeObjectField("resolution", value.getResolution());

        gen.writeEndObject();
    }
}
