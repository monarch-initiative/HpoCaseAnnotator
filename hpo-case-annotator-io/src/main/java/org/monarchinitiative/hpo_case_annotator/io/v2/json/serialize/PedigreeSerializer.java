package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.io.IOException;

public class PedigreeSerializer extends StdSerializer<Pedigree> {

    public PedigreeSerializer() {
        this(Pedigree.class);
    }

    public PedigreeSerializer(Class<Pedigree> t) {
        super(t);
    }

    @Override
    public void serialize(Pedigree pedigree, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeArrayFieldStart("members");
        for (PedigreeMember member : pedigree.getMembers())
            gen.writeObject(member);
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
