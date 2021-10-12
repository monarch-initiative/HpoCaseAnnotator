package org.monarchinitiative.hpo_case_annotator.io.v2.json.serialize;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo_case_annotator.model.v2.Publication;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PublicationSerializerTest {

    private static final Version VERSION = new Version(1, 0, 0, null, null, null);
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule("TestSerializer", VERSION);
        module.addSerializer(Publication.class, new PublicationSerializer());

        mapper.registerModule(module);
    }

    @Test
    public void serialize() throws Exception {
        String authors = "Ben Mahmoud A, Siala O, Mansour RB, Driss F, Baklouti-Gargouri S, Mkaouar-Rebai E, Belguith N, Fakhfakh F";
        Publication publication = Publication.of(
                Arrays.stream(authors.split(",")).map(String::trim).toList(),
                "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                "Gene",
                2021,
                "532(1)",
                "13-7",
                "23954224");

        String encoded = mapper.writeValueAsString(publication);

        assertThat(encoded, equalTo("""
                {
                  "authors" : [ "Ben Mahmoud A", "Siala O", "Mansour RB", "Driss F", "Baklouti-Gargouri S", "Mkaouar-Rebai E", "Belguith N", "Fakhfakh F" ],
                  "title" : "First functional analysis of a novel splicing mutation in the B3GALTL gene by an ex vivo approach in Tunisian patients with typical Peters plus syndrome",
                  "journal" : "Gene",
                  "year" : 2021,
                  "volume" : "532(1)",
                  "pages" : "13-7",
                  "pmid" : "23954224"
                }"""));
    }

}