package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.io.IOException;

public class PedigreeMemberSerializer extends StdSerializer<PedigreeMember> {

    public PedigreeMemberSerializer() {
        this(PedigreeMember.class);
    }

    public PedigreeMemberSerializer(Class<PedigreeMember> t) {
        super(t);
    }

    @Override
    public void serialize(PedigreeMember pedigreeMember, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // first store the bits for the Individual
        IndividualSerializer.writeIndividualFields(pedigreeMember, gen);

        // then the bits related to PedigreeMember
        if (pedigreeMember.getPaternalId().isPresent()) // paternalId
            gen.writeStringField("paternalId", pedigreeMember.getPaternalId().get());
        else
            gen.writeNullField("paternalId");

        if (pedigreeMember.getMaternalId().isPresent()) // maternalId
            gen.writeStringField("maternalId", pedigreeMember.getMaternalId().get());
        else
            gen.writeNullField("maternalId");

        gen.writeBooleanField("isProband", pedigreeMember.isProband());

        gen.writeEndObject();
    }
}
