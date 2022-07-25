package org.monarchinitiative.hpo_case_annotator.io.v2.json.deserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StudyMetadataDeserializerTest {


    @Test
    public void deserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(EditHistory.class, new EditHistoryDeserializer());
        module.addDeserializer(StudyMetadata.class, new StudyMetadataDeserializer());
        objectMapper.registerModule(module);

        String payload = """
                {
                  "freeText" : "Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\\ngDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.",
                  "createdBy" : {
                    "curatorId" : "BB:walt",
                    "softwareVersion" : "2.0.0",
                    "timestamp" : "2021-01-01T12:00:00Z"
                  },
                  "modifiedBy" : [ {
                    "curatorId" : "BB:skyler",
                    "softwareVersion" : "2.0.1",
                    "timestamp" : "2021-01-02T12:00:00Z"
                  }, {
                    "curatorId" : "BB:jesse",
                    "softwareVersion" : "2.0.2",
                    "timestamp" : "2021-01-03T12:00:00Z"
                  } ]
                }""";

        StudyMetadata studyMetadata = objectMapper.readValue(payload, StudyMetadata.class);

        assertThat(studyMetadata.getFreeText(), equalTo("Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\n" +
                "gDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level."));
        assertThat(studyMetadata.getCreatedBy(), equalTo(EditHistory.of("BB:walt", "2.0.0", Instant.parse("2021-01-01T12:00:00Z"))));
        assertThat(studyMetadata.getModifiedBy(), equalTo(List.of(
                EditHistory.of("BB:skyler", "2.0.1", Instant.parse("2021-01-02T12:00:00Z")),
                EditHistory.of("BB:jesse", "2.0.2", Instant.parse("2021-01-03T12:00:00Z"))
        )));
    }
}