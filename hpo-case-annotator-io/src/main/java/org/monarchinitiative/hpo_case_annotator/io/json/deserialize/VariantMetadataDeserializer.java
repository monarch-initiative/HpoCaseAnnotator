package org.monarchinitiative.hpo_case_annotator.io.json.deserialize;

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

        JsonNode validationNode = node.get("validation");

        VariantMetadataContext validationContext = VariantMetadataContext.valueOf(node.get("validationContext").asText());
        return switch (validationContext) {
            case UNKNOWN -> codec.treeToValue(validationNode, VariantMetadata.class);
            case MENDELIAN -> codec.treeToValue(validationNode, MendelianVariantMetadata.class);
            case SOMATIC -> null;  // TODO - implement
            case SPLICING -> codec.treeToValue(validationNode, SplicingVariantMetadata.class);
            case STRUCTURAL -> codec.treeToValue(validationNode, StructuralVariantMetadata.class);
        };
    }
}
