package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.Genotype;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.VariantGenotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class IndividualDeserializer extends StdDeserializer<Individual> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualDeserializer.class);

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

        List<VariantGenotype> genotypes = new ArrayList<>();
        Iterator<JsonNode> genotypesIterator = node.get("genotypes").elements();
        while (genotypesIterator.hasNext()) {
            JsonNode genotypeNode = genotypesIterator.next();
            String md5Hex;
            if (genotypeNode.has("variantMd5Hex")) {
                md5Hex = genotypeNode.get("variantMd5Hex").asText();
            } else if (genotypeNode.has("variantId")) {
                md5Hex = genotypeNode.get("variantId").asText();
                LOGGER.warn("MD5 hex used in deprecated field `variantId`. Convert the data to the latest version ASAP");
            } else {
                LOGGER.warn("Missing MD5 hex for variant genotype");
                continue;
            }
            genotypes.add(VariantGenotype.of(md5Hex, Genotype.valueOf(genotypeNode.get("genotype").asText())));
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
                genotypes,
                age,
                sex);
    }
}
