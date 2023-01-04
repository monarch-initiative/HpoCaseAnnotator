package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.SomaticVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;

import java.io.IOException;

public class SomaticVariantMetadataDeserializer extends StdDeserializer<SomaticVariantMetadata> {

    public SomaticVariantMetadataDeserializer() {
        this(SomaticVariantMetadata.class);
    }

    public SomaticVariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SomaticVariantMetadata deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        VariantMetadata variantMetadata = CuratedVariantDeserializer.deserializeVariantMetadata(node);

        String regulator = node.get("regulator").asText();
        String reporterRegulation = node.get("reporterRegulation").asText();
        String reporterResidualActivity = node.get("reporterResidualActivity").asText();
        String emsaGeneId = node.get("emsaGeneId").asText();
        String otherChoices = node.get("otherChoices").asText();
        int nPatients = node.get("nPatients").asInt();
        int mPatients = node.get("mPatients").asInt();

        return SomaticVariantMetadata.of(
                variantMetadata.getSnippet(),
                variantMetadata.getVariantClass(),
                variantMetadata.getPathomechanism(),
                variantMetadata.hasCosegregation(),
                variantMetadata.hasComparability(),
                regulator,
                reporterRegulation,
                reporterResidualActivity,
                emsaGeneId,
                otherChoices,
                nPatients,
                mPatients);
    }
}
