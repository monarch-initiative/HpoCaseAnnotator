package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.metadata.VariantMetadata;
import org.monarchinitiative.svart.Breakend;
import org.monarchinitiative.svart.BreakendVariant;
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

        if (curatedVariant instanceof BreakendVariant bv) {
            Breakend left = bv.left();
            gen.writeStringField("leftContigName", left.contig().genBankAccession());
            gen.writeStringField("leftStrand", left.strand().toString());
            // TODO - confidence intervals
            gen.writeNumberField("leftStart", left.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeNumberField("leftEnd", left.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeStringField("leftId", left.id());

            Breakend right = bv.right();
            gen.writeStringField("rightContigName", right.contig().genBankAccession());
            gen.writeStringField("rightStrand", right.strand().toString());
            // TODO - confidence intervals
            gen.writeNumberField("rightStart", right.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeNumberField("rightEnd", right.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeStringField("rightId", right.id());

            gen.writeStringField("eventId", bv.eventId());
        } else {
            gen.writeStringField("contigName", curatedVariant.contig().genBankAccession());
            gen.writeStringField("strand", curatedVariant.strand().toString());
            // TODO - confidence intervals
            gen.writeNumberField("start", curatedVariant.startWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeNumberField("end", curatedVariant.endWithCoordinateSystem(CoordinateSystem.zeroBased()));
            gen.writeNumberField("changeLength", curatedVariant.changeLength());
            gen.writeStringField("id", curatedVariant.id());
        }
        gen.writeStringField("ref", curatedVariant.ref());
        gen.writeStringField("alt", curatedVariant.alt());

        // validation
        gen.writeObjectFieldStart("variantValidation");
        VariantMetadata variantMetadata = curatedVariant.metadata();
        gen.writeStringField("validationContext", variantMetadata.getVariantMetadataContext().toString());
        gen.writeObjectField("validationMetadata", variantMetadata); // metadata have default serializers
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
