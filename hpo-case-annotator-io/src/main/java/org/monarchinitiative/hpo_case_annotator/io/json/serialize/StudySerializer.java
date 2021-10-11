package org.monarchinitiative.hpo_case_annotator.io.json.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Individual;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.hpo_case_annotator.model.v2.variant.CuratedVariant;

import java.io.IOException;
import java.util.Comparator;

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

        gen.writeObjectField("studyMetadata", study.studyMetadata());
        gen.writeObjectField("publication", study.publication());

        gen.writeArrayFieldStart("variants");
        for (CuratedVariant variant : study.variants())
            gen.writeObject(variant);
        gen.writeEndArray();

        if (study instanceof FamilyStudy familyStudy) {
            gen.writeObjectField("pedigree", familyStudy.pedigree());
        } else if (study instanceof CohortStudy cohortStudy) {
            gen.writeArrayFieldStart("individuals");
            for (Individual individual : cohortStudy.individuals().stream()
                    .sorted(Comparator.comparing(Individual::id))
                    .toList()) {
                gen.writeObject(individual);
            }
            gen.writeEndArray();
        }

        gen.writeEndObject();
    }
}
