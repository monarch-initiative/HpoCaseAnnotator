package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.StructuralVariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;

import java.io.IOException;

public class StructuralVariantMetadataDeserializer extends StdDeserializer<StructuralVariantMetadata> {

    public StructuralVariantMetadataDeserializer() {
        this(StructuralVariantMetadata.class);
    }

    protected StructuralVariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StructuralVariantMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        VariantMetadata metadata = CuratedVariantDeserializer.deserializeVariantMetadata(node);
        return StructuralVariantMetadata.of(metadata.getSnippet(),
                metadata.getVariantClass(),
                metadata.getPathomechanism(),
                metadata.hasCosegregation(),
                metadata.hasComparability());
    }
}
