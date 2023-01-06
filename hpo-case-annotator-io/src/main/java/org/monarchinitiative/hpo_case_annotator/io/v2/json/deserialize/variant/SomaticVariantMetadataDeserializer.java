package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.Util;
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

        String regulator = Util.readNullableString(node, "regulator");
        String reporterRegulation = Util.readNullableString(node, "reporterRegulation");
        String reporterResidualActivity = Util.readNullableString(node, "reporterResidualActivity");
        String emsaGeneId = Util.readNullableString(node, "emsaGeneId");
        String otherChoices = Util.readNullableString(node, "otherChoices");
        int nPatients = node.get("nPatients").asInt();
        int mPatients = node.get("mPatients").asInt();

        return SomaticVariantMetadata.of(
                variantMetadata.getSnippet(),
                variantMetadata.getVariantClass(),
                variantMetadata.getPathomechanism(),
                variantMetadata.isCosegregation(),
                variantMetadata.isComparability(),
                regulator,
                reporterRegulation,
                reporterResidualActivity,
                emsaGeneId,
                otherChoices,
                nPatients,
                mPatients);
    }
}
