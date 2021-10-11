package org.monarchinitiative.hpo_case_annotator.io.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;

import java.io.IOException;

public class DefaultVariantMetadataDeserializer extends StdDeserializer<VariantMetadata> {

    public DefaultVariantMetadataDeserializer() {
        this(VariantMetadata.class);
    }

    protected DefaultVariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VariantMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        return CuratedVariantDeserializer.deserializeVariantMetadata(node);
    }
}
