package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicObservation;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

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
        List<PhenotypicFeature> sortedById = observation.phenotypicFeatures().stream()
                .sorted(Comparator.comparing(pf -> pf.termId().getId()))
                .toList();
        for (PhenotypicFeature phenotypicFeature : sortedById) {
            gen.writeObject(phenotypicFeature);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
