package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class AgeTest {

    @ParameterizedTest
    @CsvSource({
            "1,       2,    3, P1Y2M3D",
            "1,    null, null, P1Y",
            "null,    2, null, P2M",
            "null, null,   13, P13D",
            "null, null,    0, P0D", // empty duration (birth)
    })
    public void getIso8601Duration(@ConvertWith(NullableIntConverter.class) Integer years,
                                   @ConvertWith(NullableIntConverter.class) Integer months,
                                   @ConvertWith(NullableIntConverter.class) Integer days,
                                   String expected) {
        Age age = Age.of(years, months, days);
        Optional<String> durationOptional = age.getIso8601Duration();

        assertThat(durationOptional.isPresent(), equalTo(true));
        assertThat(durationOptional.get(), equalTo(expected));
    }

    @Test
    public void getIso8601Duration_empty() {
        Age age = Age.of(null, null, null);
        Optional<String> durationOptional = age.getIso8601Duration();

        assertThat(durationOptional.isEmpty(), equalTo(true));
    }
}