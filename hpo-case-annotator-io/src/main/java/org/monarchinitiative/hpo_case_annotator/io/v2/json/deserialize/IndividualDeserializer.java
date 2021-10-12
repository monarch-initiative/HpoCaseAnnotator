package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Disease;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicObservation;
import org.monarchinitiative.hpo_case_annotator.model.v2.Sex;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.io.IOException;
import java.time.Period;
import java.util.*;

public class IndividualDeserializer extends StdDeserializer<Individual> {

    public IndividualDeserializer() {
        this(Individual.class);
    }

    protected IndividualDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Individual deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        String id = node.get("id").asText();

        JsonNode ageNode = node.get("age");
        Period age = (ageNode.isTextual()) ? Period.parse(ageNode.asText()) : null;

        List<Disease> diseases = new LinkedList<>();
        Iterator<JsonNode> diseasesIterator = node.get("diseases").elements();
        while (diseasesIterator.hasNext()) {
            diseases.add(codec.treeToValue(diseasesIterator.next(), Disease.class));
        }

        Map<String, Genotype> genotypeMap = new HashMap<>();
        Iterator<JsonNode> genotypesIterator = node.get("genotypes").elements();
        while (genotypesIterator.hasNext()) {
            JsonNode genotypeNode = genotypesIterator.next();
            genotypeMap.put(genotypeNode.get("variantId").asText(), Genotype.valueOf(genotypeNode.get("genotype").asText()));
        }

        List<PhenotypicObservation> phenotypicObservations = new LinkedList<>();
        Iterator<JsonNode> phenotypicObservationsIterator = node.get("phenotypicObservations").elements();
        while (phenotypicObservationsIterator.hasNext()) {
            phenotypicObservations.add(codec.treeToValue(phenotypicObservationsIterator.next(), PhenotypicObservation.class));
        }

        Sex sex = Sex.valueOf(node.get("sex").asText());

        return Individual.of(id, age, phenotypicObservations, diseases, genotypeMap, sex);
    }
}
