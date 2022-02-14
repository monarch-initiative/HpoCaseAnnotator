package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.GenomicBreakend;
import org.monarchinitiative.svart.GenomicBreakendVariant;
import org.monarchinitiative.svart.CoordinateSystem;

import java.io.IOException;

public class CuratedVariantSerializer extends StdSerializer<CuratedVariant> {

    public CuratedVariantSerializer() {
        this(CuratedVariant.class);
    }

    public CuratedVariantSerializer(Class<CuratedVariant> t) {
        super(t);
    }

    @Override
    public void serialize(CuratedVariant curatedVariant, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("variantType", curatedVariant.variantType().toString());
        gen.writeStringField("genomicAssemblyAccession", curatedVariant.genomicAssembly());
        if (curatedVariant.variant() instanceof GenomicBreakendVariant bv) {
            GenomicBreakend left = bv.left();
            gen.writeStringField("leftContigGenBankAccession", left.contig().genBankAccession());
            gen.writeStringField("leftStrand", left.strand().toString());
            gen.writeNumberField("leftStart", left.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!left.startConfidenceInterval().isPrecise())
                gen.writeObjectField("leftStartCi", left.startConfidenceInterval());
            gen.writeNumberField("leftEnd", left.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!left.endConfidenceInterval().isPrecise())
                gen.writeObjectField("leftEndCi", left.endConfidenceInterval());
            gen.writeStringField("leftId", left.id());

            GenomicBreakend right = bv.right();
            gen.writeStringField("rightContigGenBankAccession", right.contig().genBankAccession());
            gen.writeStringField("rightStrand", right.strand().toString());
            gen.writeNumberField("rightStart", right.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!right.startConfidenceInterval().isPrecise())
                gen.writeObjectField("rightStartCi", right.startConfidenceInterval());
            gen.writeNumberField("rightEnd", right.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!right.endConfidenceInterval().isPrecise())
                gen.writeObjectField("rightEndCi", right.endConfidenceInterval());
            gen.writeStringField("rightId", right.id());

            gen.writeStringField("eventId", bv.eventId());
        } else {
            gen.writeStringField("contigGenBankAccession", curatedVariant.contig().genBankAccession());
            gen.writeStringField("strand", curatedVariant.strand().toString());
            gen.writeNumberField("start", curatedVariant.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!curatedVariant.coordinates().startConfidenceInterval().isPrecise())
                gen.writeObjectField("startCi", curatedVariant.coordinates().startConfidenceInterval());
            gen.writeNumberField("end", curatedVariant.endWithCoordinateSystem(CoordinateSystem.zeroBased()));
            if (!curatedVariant.coordinates().endConfidenceInterval().isPrecise())
                gen.writeObjectField("endCi", curatedVariant.coordinates().endConfidenceInterval());
            gen.writeNumberField("changeLength", curatedVariant.changeLength());
            gen.writeStringField("id", curatedVariant.id());
        }
        gen.writeStringField("md5Hex", curatedVariant.md5Hex());
        gen.writeStringField("ref", curatedVariant.ref());
        gen.writeStringField("alt", curatedVariant.alt());

        // validation
        gen.writeObjectFieldStart("variantValidation");
        VariantMetadata variantMetadata = curatedVariant.variantMetadata();
        gen.writeStringField("validationContext", variantMetadata.getVariantMetadataContext().toString());
        gen.writeObjectField("validationMetadata", variantMetadata); // metadata have default serializers
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
