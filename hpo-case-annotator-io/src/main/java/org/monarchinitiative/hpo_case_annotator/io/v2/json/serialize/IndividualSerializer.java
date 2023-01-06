package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.DiseaseStatus;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;

import java.io.IOException;

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
        gen.writeObjectField("vitalStatus", individual.getVitalStatus());

        gen.writeArrayFieldStart("diseases");
        for (DiseaseStatus diseaseStatus : individual.getDiseaseStates())
            gen.writeObject(diseaseStatus);
        gen.writeEndArray();

        gen.writeArrayFieldStart("genotypes");
        for (VariantGenotype vg : individual.getGenotypes()) {
            gen.writeStartObject();
            gen.writeStringField("variantMd5Hex", vg.getMd5Hex());
            gen.writeStringField("genotype", vg.getGenotype().toString());
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
