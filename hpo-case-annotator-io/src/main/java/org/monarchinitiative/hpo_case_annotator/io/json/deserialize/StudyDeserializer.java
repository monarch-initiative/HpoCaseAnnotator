package org.monarchinitiative.hpo_case_annotator.io.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StudyDeserializer extends StdDeserializer<Study> {

    public StudyDeserializer() {
        this(Study.class);
    }

    protected StudyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Study deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        Publication publication = codec.treeToValue(node.get("publication"), Publication.class);
        StudyMetadata studyMetadata = codec.treeToValue(node.get("studyMetadata"), StudyMetadata.class);

        Iterator<JsonNode> iterator = node.get("variants").elements();
        List<CuratedVariant> variants = new LinkedList<>();
        while (iterator.hasNext()) {
            CuratedVariant variant = codec.treeToValue(iterator.next(), CuratedVariant.class);
            variants.add(variant);
        }

        if (node.has("pedigree")) {
            // FamilyStudy
            Pedigree pedigree = codec.treeToValue(node.get("pedigree"), Pedigree.class);

            return FamilyStudy.of(publication, variants, pedigree, studyMetadata);
        } else if (node.has("individuals")) {
            List<Individual> individuals = new LinkedList<>();
            Iterator<JsonNode> individualsIterator = node.get("individuals").elements();
            while (individualsIterator.hasNext()) {
                individuals.add(codec.treeToValue(individualsIterator.next(), Individual.class));
            }

            return CohortStudy.of(publication, variants, individuals, studyMetadata);
        } else {
            throw new IOException("Invalid input. Neither `pedigree` nor `individuals` field was found");
        }
    }
}
