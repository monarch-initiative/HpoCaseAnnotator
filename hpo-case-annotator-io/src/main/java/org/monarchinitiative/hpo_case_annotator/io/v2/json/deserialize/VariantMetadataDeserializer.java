package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.*;

import java.io.IOException;

public class VariantMetadataDeserializer extends StdDeserializer<VariantMetadata> {

    public VariantMetadataDeserializer() {
        this(VariantMetadata.class);
    }

    protected VariantMetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VariantMetadata deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        JsonNode metadataNode = node.get("validationMetadata");

        VariantMetadataContext validationContext = VariantMetadataContext.valueOf(node.get("validationContext").asText());
        return switch (validationContext) {
            case UNKNOWN -> codec.treeToValue(metadataNode, VariantMetadata.class);
            case MENDELIAN -> codec.treeToValue(metadataNode, MendelianVariantMetadata.class);
            case SOMATIC -> codec.treeToValue(metadataNode, SomaticVariantMetadata.class);
            case SPLICING -> codec.treeToValue(metadataNode, SplicingVariantMetadata.class);
            case STRUCTURAL -> codec.treeToValue(metadataNode, StructuralVariantMetadata.class);
            default -> throw new IOException("Unknown validation context: " + validationContext);
        };
    }
}
