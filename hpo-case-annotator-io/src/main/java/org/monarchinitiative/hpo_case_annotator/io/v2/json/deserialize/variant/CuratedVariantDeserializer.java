package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadataContext;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.GenomicAssembly;

import java.io.IOException;
import java.util.Optional;

public class CuratedVariantDeserializer extends StdDeserializer<CuratedVariant> {

    private final GenomicAssembly[] assemblies;

    public CuratedVariantDeserializer(GenomicAssembly... assemblies) {
        super(CuratedVariant.class);
        this.assemblies = assemblies;
    }

    static VariantMetadata deserializeVariantMetadata(JsonNode jsonNode) {
        JsonNode snippetNode = jsonNode.get("snippet");
        String snippet = snippetNode == null ? null : snippetNode.asText();
        JsonNode vClassNode = jsonNode.get("variantClass");
        String variantClass = vClassNode == null ? null : vClassNode.asText();
        JsonNode pathoNode = jsonNode.get("pathomechanism");
        String pathomechanism = pathoNode == null ? null : pathoNode.asText();
        JsonNode cosegNode = jsonNode.get("cosegregation");
        boolean cosegregation = cosegNode != null && cosegNode.asBoolean();
        JsonNode comparabilityNode = jsonNode.get("comparability");
        boolean comparability = comparabilityNode != null && comparabilityNode.asBoolean();

        return VariantMetadata.of(VariantMetadataContext.UNKNOWN, snippet, variantClass, pathomechanism, cosegregation, comparability);
    }

    private static ConfidenceInterval parseConfidenceInterval(String ciFieldName, JsonNode node, ObjectCodec codec) throws JsonProcessingException {
        return (node.has(ciFieldName))
                ? codec.treeToValue(node.get(ciFieldName), ConfidenceInterval.class)
                : ConfidenceInterval.precise();
    }

    @Override
    public CuratedVariant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        String ref = node.get("ref").asText();
        String alt = node.get("alt").asText();
        String md5Hex = node.get("md5Hex").asText();
        String genomicAssemblyAccession = node.get("genomicAssemblyAccession").asText();

        VariantMetadata variantMetadata = codec.treeToValue(node.get("variantValidation"), VariantMetadata.class);

        String variantType = node.get("variantType").asText();
        GenomicVariant variant;
        if (variantType.equals("BND")) {
            // breakend variant

            // left
            String leftContigGenBankAccession = node.get("leftContigGenBankAccession").asText();
            Optional<Contig> leftContigOptional = findContig(leftContigGenBankAccession);
            if (leftContigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + leftContigGenBankAccession + "`");
            String leftId = node.get("leftId").asText();
            Strand leftStrand = Strand.parseStrand(node.get("leftStrand").asText());
            ConfidenceInterval leftStartCi = parseConfidenceInterval("leftStartCi", node, codec);
            ConfidenceInterval leftEndCi = parseConfidenceInterval("leftEndCi", node, codec);
            Coordinates leftCoordinates = Coordinates.of(CoordinateSystem.zeroBased(),
                    node.get("leftStart").asInt(),
                    leftStartCi,
                    node.get("leftEnd").asInt(),
                    leftEndCi);
            GenomicBreakend left = GenomicBreakend.of(leftContigOptional.get(), leftId, leftStrand, leftCoordinates);

            // right
            String rightContigGenBankAccession = node.get("rightContigGenBankAccession").asText();
            Optional<Contig> rightContigOptional = findContig(rightContigGenBankAccession);
            if (rightContigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + rightContigGenBankAccession + "`");
            String rightId = node.get("rightId").asText();
            Strand rightStrand = Strand.parseStrand(node.get("rightStrand").asText());
            ConfidenceInterval rightStartCi = parseConfidenceInterval("rightStartCi", node, codec);
            ConfidenceInterval rightEndCi = parseConfidenceInterval("rightEndCi", node, codec);
            Coordinates rightCoordinates = Coordinates.of(CoordinateSystem.zeroBased(),
                    node.get("rightStart").asInt(),
                    rightStartCi,
                    node.get("rightEnd").asInt(),
                    rightEndCi);
            GenomicBreakend right = GenomicBreakend.of(rightContigOptional.get(), rightId, rightStrand, rightCoordinates);

            String eventId = node.get("eventId").asText();
            variant = GenomicVariant.of(eventId,
                    left,
                    right,
                    ref,
                    alt);
        } else {
            // sequence or symbolic
            String contigGenBankAccession = node.get("contigGenBankAccession").asText();
            Optional<Contig> contigOptional = findContig(contigGenBankAccession);
            if (contigOptional.isEmpty())
                throw new IllegalArgumentException("Unknown contig `" + contigGenBankAccession + "`");

            String id = node.get("id").asText();
            Strand strand = Strand.parseStrand(node.get("strand").asText());

            ConfidenceInterval startCi = parseConfidenceInterval("startCi", node, codec);
            ConfidenceInterval endCi = parseConfidenceInterval("endCi", node, codec);

            Coordinates coordinates = Coordinates.of(CoordinateSystem.zeroBased(), node.get("start").asInt(), startCi, node.get("end").asInt(), endCi);
            int changeLength = node.get("changeLength").asInt();

            if (VariantType.isSymbolic(ref, alt)) {
                variant = GenomicVariant.of(contigOptional.get(),
                        id,
                        strand,
                        coordinates,
                        ref,
                        alt,
                        changeLength);
            } else {
                variant = GenomicVariant.of(contigOptional.get(),
                        id,
                        strand,
                        coordinates,
                        ref,
                        alt);
            }
        }

        CuratedVariant curatedVariant = CuratedVariant.of(genomicAssemblyAccession,
                variant,
                variantMetadata);
        if (!md5Hex.equals(curatedVariant.md5Hex())) {
            throw new IOException("MD5 checksum mismatch for variant. Expected: `" + md5Hex + "`, actual: `" + curatedVariant.md5Hex() + "`. Variant data: `" + node.asText() + "`");
        }
        return curatedVariant;
    }

    private Optional<Contig> findContig(String genBankAccession) {
        for (GenomicAssembly assembly : assemblies) {
            Contig contig = assembly.contigByName(genBankAccession);
            if (!contig.isUnknown())
                return Optional.of(contig);
        }
        return Optional.empty();
    }
}
