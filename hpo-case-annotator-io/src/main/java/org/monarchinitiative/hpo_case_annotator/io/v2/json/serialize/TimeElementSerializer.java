package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.TimeElement;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;

public class TimeElementSerializer extends StdSerializer<TimeElement> {

    public TimeElementSerializer() {
        this(TimeElement.class);
    }

    public TimeElementSerializer(Class<TimeElement> t) {
        super(t);
    }

    @Override
    public void serialize(TimeElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        TimeElement.TimeElementCase teCase = value.getTimeElementCase();
        if (teCase != null) {
            gen.writeStringField("timeCase", teCase.name());
            switch (teCase) {
                case GESTATIONAL_AGE -> gen.writeObjectField("gestationalAge", value.getGestationalAge());
                case AGE -> gen.writeObjectField("age", value.getAge());
                case AGE_RANGE -> gen.writeObjectField("ageRange", value.getAgeRange());
                case ONTOLOGY_CLASS -> {
                    TermId ontologyClass = value.getOntologyClass();
                    if (ontologyClass != null)
                        gen.writeStringField("ontologyClass", ontologyClass.getValue());
                }
            }
        }

        gen.writeEndObject();
    }
}
