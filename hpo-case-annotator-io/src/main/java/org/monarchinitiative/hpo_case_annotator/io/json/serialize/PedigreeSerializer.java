package org.monarchinitiative.hpo_case_annotator.io.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.Pedigree;
import org.monarchinitiative.hpo_case_annotator.model.v2.PedigreeMember;

import java.io.IOException;
import java.util.Comparator;

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

        for (PedigreeMember member : pedigree.members().stream()
                .sorted(Comparator.comparing(Individual::id))
                .toList())
            gen.writeObject(member);
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
