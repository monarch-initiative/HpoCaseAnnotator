package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Disease;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicObservation;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.io.IOException;
import java.util.Map;

public class IndividualSerializer extends StdSerializer<Individual> {

    public IndividualSerializer() {
        this(Individual.class);
    }

    public IndividualSerializer(Class<Individual> t) {
        super(t);
    }

    static void writeIndividualFields(Individual individual, JsonGenerator gen) throws IOException {
        gen.writeStringField("id", individual.id());

        if (individual.age().isPresent()) // age
            gen.writeStringField("age", individual.age().get().toString());
        else
            gen.writeNullField("age");

        gen.writeArrayFieldStart("diseases");
        for (Disease disease : individual.diseases())
            gen.writeObject(disease);
        gen.writeEndArray();

        gen.writeArrayFieldStart("genotypes");
        for (Map.Entry<String, Genotype> entry : individual.genotypes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            gen.writeStartObject();
            gen.writeStringField("variantId", entry.getKey());
            gen.writeStringField("genotype", entry.getValue().toString());
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("phenotypicObservations");
        for (PhenotypicObservation phenotypicObservation : individual.phenotypicObservations())
            gen.writeObject(phenotypicObservation);
        gen.writeEndArray();

        gen.writeObjectField("sex", individual.sex());
    }

    @Override
    public void serialize(Individual individual, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeIndividualFields(individual, gen);

        gen.writeEndObject();
    }
}
