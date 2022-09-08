package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.*;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.io.IOException;

public class StudySerializer extends StdSerializer<Study> {

    public StudySerializer() {
        this(Study.class);
    }

    public StudySerializer(Class<Study> t) {
        super(t);
    }

    @Override
    public void serialize(Study study, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("id", study.getId());
        gen.writeObjectField("studyMetadata", study.getStudyMetadata());
        gen.writeObjectField("publication", study.getPublication());

        gen.writeArrayFieldStart("variants");
        for (CuratedVariant variant : study.getVariants())
            gen.writeObject(variant);
        gen.writeEndArray();

        if (study instanceof FamilyStudy familyStudy) {
            gen.writeObjectField("pedigree", familyStudy.getPedigree());
        } else if (study instanceof CohortStudy cohortStudy) {
            gen.writeArrayFieldStart("individuals");
            for (Individual individual : cohortStudy.getMembers())
                gen.writeObject(individual);
            gen.writeEndArray();
        } else if (study instanceof IndividualStudy individualStudy) {
            gen.writeObjectField("individual", individualStudy.getIndividual());
        } else {
            throw new IOException("Invalid study `%s`".formatted(study.getClass().getCanonicalName()));
        }

        gen.writeEndObject();
    }
}
