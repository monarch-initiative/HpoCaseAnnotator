package org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.MendelianVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;

import java.io.IOException;

public class MendelianVariantMetadataDeserializer extends StdDeserializer<MendelianVariantMetadata> {

    public MendelianVariantMetadataDeserializer() {
        this(MendelianVariantMetadata.class);
    }

    protected MendelianVariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MendelianVariantMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        VariantMetadata variantMetadata = CuratedVariantDeserializer.deserializeVariantMetadata(node);

        String regulator = node.get("regulator").asText();
        String reporterRegulation = node.get("reporterRegulation").asText();
        String reporterResidualActivity = node.get("reporterResidualActivity").asText();
        boolean emsaValidationPerformed = node.get("emsaValidationPerformed").asBoolean();
        String emsaTfSymbol = node.get("emsaTfSymbol").asText();
        String emsaGeneId = node.get("emsaGeneId").asText();
        String otherChoices = node.get("otherChoices").asText();
        String otherEffect = node.get("otherEffect").asText();

        return MendelianVariantMetadata.of(
                variantMetadata.getSnippet(),
                variantMetadata.getVariantClass(),
                variantMetadata.getPathomechanism(),
                variantMetadata.isCosegregation(),
                variantMetadata.isComparability(),
                regulator,
                reporterRegulation,
                reporterResidualActivity,
                emsaValidationPerformed,
                emsaTfSymbol,
                emsaGeneId,
                otherChoices,
                otherEffect);
    }
}
