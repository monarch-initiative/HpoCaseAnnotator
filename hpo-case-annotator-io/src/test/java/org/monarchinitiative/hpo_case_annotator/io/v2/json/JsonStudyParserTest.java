package org.monarchinitiative.hpo_case_annotator.io.v2.json;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.monarchinitiative.hpo_case_annotator.model.v2.CohortStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.FamilyStudy;
import org.monarchinitiative.hpo_case_annotator.model.v2.Study;
import org.monarchinitiative.hpo_case_annotator.test.TestData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@DisabledOnOs(OS.WINDOWS)
public class JsonStudyParserTest {

    private static String readInputStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private static String getCohortStudyPayload() throws IOException {
        return readInputStream(JsonStudyParserTest.class.getResourceAsStream("test-model-v2-cohort.json"));
    }

    private static String getFamilyStudyPayload() throws IOException {
        return readInputStream(JsonStudyParserTest.class.getResourceAsStream("test-model-v2-family.json"));
    }

    @Nested
    public class FamilyStudyTest {

        @Test
        public void deserialize() throws Exception {
            JsonStudyParser parser = JsonStudyParser.getInstance();

            Study actual = parser.read(getFamilyStudyPayload());

            FamilyStudy expected = TestData.V2.comprehensiveFamilyStudy();
            assertThat(actual, equalTo(expected));
        }

        @Test
        public void roundTrip() throws IOException {
            FamilyStudy familyStudy = TestData.V2.comprehensiveFamilyStudy();

            JsonStudyParser jsonStudyParser = JsonStudyParser.getInstance();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            jsonStudyParser.write(familyStudy, os);
            Study deserialized = jsonStudyParser.read(os.toString());

            assertThat(familyStudy, equalTo(deserialized));
        }

        @Test
        @Disabled
        // Used to generate paylod to compare with in deserialize test.
        // Note that content of `TestData` is the single source of truth.
        public void dump() throws Exception {
            Path path = Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/io/v2/json/test-model-v2-family.json");
            FamilyStudy study = TestData.V2.comprehensiveFamilyStudy();

            JsonStudyParser parser = JsonStudyParser.getInstance();
            try (OutputStream os = Files.newOutputStream(path)) {
                parser.write(study, os);
            }
        }
    }

    @Nested
    public class CohortStudyTest {

        @Test
        public void deserialize() throws Exception {
            JsonStudyParser parser = JsonStudyParser.getInstance();

            Study actual = parser.read(getCohortStudyPayload());

            CohortStudy expected = TestData.V2.comprehensiveCohortStudy();
            assertThat(actual, equalTo(expected));
        }

        @Test
        public void roundTrip() throws IOException {
            CohortStudy cohortStudy = TestData.V2.comprehensiveCohortStudy();

            JsonStudyParser jsonStudyParser = JsonStudyParser.getInstance();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            jsonStudyParser.write(cohortStudy, os);
            Study deserialized = jsonStudyParser.read(os.toString());

            assertThat(cohortStudy, equalTo(deserialized));
        }

        @Test
        @Disabled
        // Used to generate paylod to compare with in deserialize test.
        // Note that content of `TestData` is the single source of truth.
        public void dump() throws Exception {
            Path path = Paths.get("src/test/resources/org/monarchinitiative/hpo_case_annotator/io/v2/json/test-model-v2-cohort.json");
            CohortStudy study = TestData.V2.comprehensiveCohortStudy();

            JsonStudyParser parser = JsonStudyParser.getInstance();
            try (OutputStream os = Files.newOutputStream(path)) {
                parser.write(study, os);
            }
        }
    }

}