package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.AgeRange;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicFeature;
import org.monarchinitiative.hpo_case_annotator.model.v2.PhenotypicObservation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PhenotypicObservationDeserializer extends StdDeserializer<PhenotypicObservation> {

    public PhenotypicObservationDeserializer() {
        this(PhenotypicObservation.class);
    }

    protected PhenotypicObservationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PhenotypicObservation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        AgeRange observationAge = codec.treeToValue(node.get("observationAge"), AgeRange.class);

        Set<PhenotypicFeature> phenotypicFeatures = new HashSet<>();
        Iterator<JsonNode> phenotypicFeaturesIterator = node.get("phenotypicFeatures").elements();
        while (phenotypicFeaturesIterator.hasNext()) {
            PhenotypicFeature phenotypicFeature = codec.treeToValue(phenotypicFeaturesIterator.next(), PhenotypicFeature.class);
            phenotypicFeatures.add(phenotypicFeature);
        }

        return PhenotypicObservation.of(observationAge, phenotypicFeatures);
    }
}
