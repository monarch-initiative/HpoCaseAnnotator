package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.Util;
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

        String regulator = Util.readNullableString(node, "regulator");
        String reporterRegulation = Util.readNullableString(node, "reporterRegulation");
        String reporterResidualActivity = Util.readNullableString(node, "reporterResidualActivity");
        boolean emsaValidationPerformed = node.get("emsaValidationPerformed").asBoolean();
        String emsaTfSymbol = Util.readNullableString(node, "emsaTfSymbol");
        String emsaGeneId = Util.readNullableString(node, "emsaGeneId");
        String otherChoices = Util.readNullableString(node, "otherChoices");
        String otherEffect = Util.readNullableString(node, "otherEffect");

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
