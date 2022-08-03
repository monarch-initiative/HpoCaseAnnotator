package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;

import java.io.IOException;
import java.util.*;

public class IndividualDeserializer extends StdDeserializer<Individual> {

    public IndividualDeserializer() {
        this(Individual.class);
    }

    protected IndividualDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Individual deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        String id = node.get("id").asText();

        // TODO - can fail due to age being previously stored as `Age`
        TimeElement age = codec.treeToValue(node.get("age"), TimeElement.class);

        List<DiseaseStatus> diseaseStatuses = new LinkedList<>();
        Iterator<JsonNode> diseasesIterator = node.get("diseases").elements();
        while (diseasesIterator.hasNext()) {
            diseaseStatuses.add(codec.treeToValue(diseasesIterator.next(), DiseaseStatus.class));
        }

        Map<String, Genotype> genotypeMap = new HashMap<>();
        Iterator<JsonNode> genotypesIterator = node.get("genotypes").elements();
        while (genotypesIterator.hasNext()) {
            JsonNode genotypeNode = genotypesIterator.next();
            genotypeMap.put(genotypeNode.get("variantId").asText(), Genotype.valueOf(genotypeNode.get("genotype").asText()));
        }

        List<PhenotypicFeature> phenotypicFeatures = new LinkedList<>();
        Iterator<JsonNode> phenotypicFeaturesIterator = node.get("phenotypicFeatures").elements();
        while (phenotypicFeaturesIterator.hasNext()) {
            phenotypicFeatures.add(codec.treeToValue(phenotypicFeaturesIterator.next(), PhenotypicFeature.class));
        }

        Sex sex = Sex.valueOf(node.get("sex").asText());

        return Individual.of(id,
                phenotypicFeatures,
                diseaseStatuses,
                genotypeMap,
                age,
                sex);
    }
}
