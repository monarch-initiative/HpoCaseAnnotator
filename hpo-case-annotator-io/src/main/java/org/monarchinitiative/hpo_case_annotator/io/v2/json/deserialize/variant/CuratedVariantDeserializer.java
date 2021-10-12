package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadataContext;
import org.monarchinitiative.svart.*;

import java.io.IOException;
import java.util.Optional;

public class CuratedVariantDeserializer extends StdDeserializer<CuratedVariant> {

    private final GenomicAssembly hg19, hg38;

    public CuratedVariantDeserializer(GenomicAssembly hg19, GenomicAssembly hg38) {
        super(CuratedVariant.class);
        this.hg19 = hg19;
        this.hg38 = hg38;
    }

    static VariantMetadata deserializeVariantMetadata(JsonNode jsonNode) {
        String snippet = jsonNode.get("snippet").asText();
        String variantClass = jsonNode.get("variantClass").asText();
        String pathomechanism = jsonNode.get("pathomechanism").asText();
        boolean cosegregation = jsonNode.get("cosegregation").asBoolean();
        boolean comparability = jsonNode.get("comparability").asBoolean();

        return VariantMetadata.of(VariantMetadataContext.UNKNOWN, snippet, variantClass, pathomechanism, cosegregation, comparability);
    }

    @Override
    public CuratedVariant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        String ref = node.get("ref").asText();
        String alt = node.get("alt").asText();

        VariantMetadata variantMetadata = codec.treeToValue(node.get("variantValidation"), VariantMetadata.class);

        String variantType = node.get("variantType").asText();
        if (variantType.equals("BND")) {
            // breakend variant

            // left
            String leftContigName = node.get("leftContigName").asText();
            Optional<Contig> leftContigOptional = findContig(leftContigName);
            if (leftContigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + leftContigName + "`");
            String leftId = node.get("leftId").asText();
            Strand leftStrand = Strand.parseStrand(node.get("leftStrand").asText());
            Coordinates leftCoordinates = Coordinates.of(CoordinateSystem.zeroBased(), node.get("leftStart").asInt(), node.get("leftEnd").asInt());
            Breakend left = Breakend.of(leftContigOptional.get(), leftId, leftStrand, leftCoordinates);

            // right
            String rightContigName = node.get("rightContigName").asText();
            Optional<Contig> rightContigOptional = findContig(rightContigName);
            if (rightContigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + rightContigName + "`");
            String rightId = node.get("rightId").asText();
            Strand rightStrand = Strand.parseStrand(node.get("rightStrand").asText());
            Coordinates rightCoordinates = Coordinates.of(CoordinateSystem.zeroBased(), node.get("rightStart").asInt(), node.get("rightEnd").asInt());
            Breakend right = Breakend.of(rightContigOptional.get(), rightId, rightStrand, rightCoordinates);

            String eventId = node.get("eventId").asText();
            return CuratedVariant.breakend(eventId, left, right, ref, alt, variantMetadata);
        } else {
            // sequence or symbolic
            String contigName = node.get("contigName").asText();
            Optional<Contig> contigOptional = findContig(contigName);
            if (contigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + contigName + "`");

            String id = node.get("id").asText();
            Strand strand = Strand.parseStrand(node.get("strand").asText());
            Coordinates coordinates = Coordinates.of(CoordinateSystem.zeroBased(), node.get("start").asInt(), node.get("end").asInt());
            int changeLength = node.get("changeLength").asInt();

            return CuratedVariant.sequenceSymbolic(contigOptional.get(), id, strand, coordinates, ref, alt, changeLength, variantMetadata);
        }
    }

    private Optional<Contig> findContig(String contigName) {
        Contig contig = hg19.contigByName(contigName);
        if (contig.isUnknown()) {
            Contig other = hg38.contigByName(contigName);
            return other.isUnknown() ? Optional.empty() : Optional.of(other);
        }
        return Optional.of(contig);
    }
}
