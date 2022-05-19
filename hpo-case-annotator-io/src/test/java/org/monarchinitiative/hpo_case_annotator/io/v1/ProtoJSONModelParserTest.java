package org.monarchinitiative.hpo_case_annotator.io.v1;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.monarchinitiative.hpo_case_annotator.model.proto.DiseaseCase;
import org.monarchinitiative.hpo_case_annotator.test.TestData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@DisabledOnOs(OS.WINDOWS)
public class ProtoJSONModelParserTest {

    @Test
    public void v1IsParsedCorrectly() throws Exception {
        DiseaseCase v1Case;
        try (InputStream is = Objects.requireNonNull(ProtoJSONModelParserTest.class.getResourceAsStream("test-model-v1-Beygo-2012-CFTR.json"),
                "Missing test file. Please report the bug to developers")) {
            v1Case = ProtoJSONModelParser.readDiseaseCase(is);
        }

        assertThat(v1Case, equalTo(TestData.V1.comprehensiveCase()));
    }

    @Test
    public void v1RoundTrip() throws Exception {
        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ProtoJSONModelParser.saveDiseaseCase(baos, diseaseCase, StandardCharsets.UTF_8);

        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        DiseaseCase actual = ProtoJSONModelParser.readDiseaseCase(is);

        assertThat(actual, equalTo(diseaseCase));
    }

    @Test
    @Disabled
    // Used to generate paylod to compare with in deserialize test.
    // Note that content of `TestData` is the single source of truth.
    public void dump() throws Exception {
        Path path = Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/io/v1/test-model-v1-Beygo-2012-CFTR.json");
        DiseaseCase diseaseCase = TestData.V1.comprehensiveCase();

        try (OutputStream os = Files.newOutputStream(path)) {
            ProtoJSONModelParser.saveDiseaseCase(os, diseaseCase, StandardCharsets.UTF_8);
        }
    }
}