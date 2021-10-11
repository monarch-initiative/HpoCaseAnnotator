package org.monarchinitiative.hpo_case_annotator.io.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicObservation;

import java.io.IOException;

public class PhenotypicObservationSerializer extends StdSerializer<PhenotypicObservation> {

    public PhenotypicObservationSerializer() {
        this(PhenotypicObservation.class);
    }

    protected PhenotypicObservationSerializer(Class<PhenotypicObservation> t) {
        super(t);
    }

    @Override
    public void serialize(PhenotypicObservation observation, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("observationAge", observation.observationAge());

        gen.writeArrayFieldStart("phenotypicFeatures");
        for (PhenotypicFeature phenotypicFeature : observation.phenotypicFeatures()) {
            gen.writeObject(phenotypicFeature);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
