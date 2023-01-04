package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.SplicingVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;

import java.io.IOException;

public class SplicingVariantMetadataDeserializer extends StdDeserializer<SplicingVariantMetadata> {

    public SplicingVariantMetadataDeserializer() {
        this(SplicingVariantMetadata.class);
    }

    protected SplicingVariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SplicingVariantMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        VariantMetadata variantMetadata = CuratedVariantDeserializer.deserializeVariantMetadata(node);

        int crypticPosition = node.get("crypticPosition").asInt();
        SplicingVariantMetadata.CrypticSpliceSiteType crypticSpliceSiteType = SplicingVariantMetadata.CrypticSpliceSiteType.valueOf(node.get("crypticSpliceSiteType").asText());
        String crypticSpliceSiteSnippet = node.get("crypticSpliceSiteSnippet").asText();
        String consequence = node.get("consequence").asText();
        boolean minigeneValidation = node.get("minigeneValidation").asBoolean();
        boolean siteDirectedMutagenesisValidation = node.get("siteDirectedMutagenesisValidation").asBoolean();
        boolean rtPcrValidation = node.get("rtPcrValidation").asBoolean();
        boolean srProteinOverexpressionValidation = node.get("srProteinOverexpressionValidation").asBoolean();
        boolean srProteinKnockdownValidation = node.get("srProteinKnockdownValidation").asBoolean();
        boolean cDnaSequencingValidation = node.get("cDnaSequencingValidation").asBoolean();
        boolean pcrValidation = node.get("pcrValidation").asBoolean();
        boolean mutOfWtSpliceSiteValidation = node.get("mutOfWtSpliceSiteValidation").asBoolean();
        boolean otherValidation = node.get("otherValidation").asBoolean();

        return SplicingVariantMetadata.of(variantMetadata.getSnippet(),
                variantMetadata.getVariantClass(),
                variantMetadata.getPathomechanism(),
                variantMetadata.hasCosegregation(),
                variantMetadata.hasComparability(),
                crypticPosition,
                crypticSpliceSiteType,
                crypticSpliceSiteSnippet,
                consequence,
                minigeneValidation,
                siteDirectedMutagenesisValidation,
                rtPcrValidation,
                srProteinOverexpressionValidation,
                srProteinKnockdownValidation,
                cDnaSequencingValidation,
                pcrValidation,
                mutOfWtSpliceSiteValidation,
                otherValidation);
    }
}
