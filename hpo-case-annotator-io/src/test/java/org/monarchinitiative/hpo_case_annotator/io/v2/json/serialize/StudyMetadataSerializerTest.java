package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.monarchinitiative.hpo_case_annotator.model.v2.EditHistory;
import org.monarchinitiative.hpo_case_annotator.model.v2.StudyMetadata;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@DisabledOnOs(OS.WINDOWS)
public class StudyMetadataSerializerTest {

    private static final Version VERSION = new Version(1, 0, 0, null, null, null);
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule("TestSerializer", VERSION);
        module.addSerializer(StudyMetadata.class, new StudyMetadataSerializer());
        module.addSerializer(EditHistory.class, new EditHistorySerializer());

        mapper.registerModule(module);
    }

    @Test
    public void serialize() throws Exception {
        String freeText = "Two Tunisian neonates presented with low birth weight, disproportionate short stature, microcephaly and facial dysmorphism including dolichocephaly, round face and bilateral corneal opacity.\n" +
                "gDNA, mRNA and minigene analysis revealed heterozygosity for mutation NM_194318.3:c.597-2A>G, which disrupts the 3'ss and causes out-of-frame exon skipping/PTC at the protein level.";
        EditHistory created = EditHistory.of("BB:walt", "2.0.0", Instant.parse("2021-01-01T12:00:00.00Z"));
        List<EditHistory> modifiedBy = List.of(
                EditHistory.of("BB:skyler", "2.0.1", Instant.parse("2021-01-02T12:00:00.00Z")),
                EditHistory.of("BB:jesse", "2.0.2", Instant.parse("2021-01-03T12:00:00.00Z"))
        );
        StudyMetadata metadata = StudyMetadata.of(freeText, created, modifiedBy);

        String encoded = mapper.writeValueAsString(metadata);

        assertThat(encoded, equalTo("""
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
                }"""));
    }
}