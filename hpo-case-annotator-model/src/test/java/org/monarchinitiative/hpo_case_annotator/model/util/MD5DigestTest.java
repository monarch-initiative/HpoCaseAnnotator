package org.monarchinitiative.hpo_case_annotator.model.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MD5DigestTest {

    @ParameterizedTest
    @CsvSource({
            "abc,          900150983cd24fb0d6963f7d28e17f72",
            "def,          4ed9407630eb1000c0f6b63842defa7d",
            "ancora-pizza, 189248684e5c5a9c595f3eb9274728a0",
    })
    public void digest(String input, String expected) {
        assertThat(MD5Digest.digest(input), equalTo(expected));
    }
}