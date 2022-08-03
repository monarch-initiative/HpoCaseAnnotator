package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
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
        gen.writeStringField("id", individual.getId());

        gen.writeObjectField("age", individual.getAge());

        gen.writeArrayFieldStart("diseases");
        for (DiseaseStatus diseaseStatus : individual.getDiseaseStates())
            gen.writeObject(diseaseStatus);
        gen.writeEndArray();

        gen.writeArrayFieldStart("genotypes");
        for (Map.Entry<String, Genotype> entry : individual.getGenotypes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            gen.writeStartObject();
            gen.writeStringField("variantId", entry.getKey());
            gen.writeStringField("genotype", entry.getValue().toString());
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("phenotypicFeatures");
        for (PhenotypicFeature phenotypicFeature : individual.getPhenotypicFeatures())
            gen.writeObject(phenotypicFeature);
        gen.writeEndArray();

        gen.writeObjectField("sex", individual.getSex());
    }

    @Override
    public void serialize(Individual individual, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeIndividualFields(individual, gen);

        gen.writeEndObject();
    }
}
