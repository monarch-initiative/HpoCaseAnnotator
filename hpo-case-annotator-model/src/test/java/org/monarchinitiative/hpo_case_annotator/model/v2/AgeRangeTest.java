package org.monarchinitiative.hpo_case_annotator.model.v2;

import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Period;

import static org.hamcrest.MatcherAssert.assertThat;

public class AgeRangeTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0,   10, 11, 25,    10, 11, 25",
            "1, 2, 3,   2,  1,  2,      0, 11, -1",
            "1, 2, 3,   1,  2,  3,      0,  0,  0",
    })
    public void length(int startYears, int startMonths, int startDays,
                       int endYears, int endMonths, int endDays,
                       int expectedYears, int expectedMonths, int expectedDays) {
        Period start = Period.of(startYears, startMonths, startDays);
        Period end = Period.of(endYears, endMonths, endDays);
        AgeRange range = AgeRange.of(start, end);

        assertThat(range.length(), Matchers.equalTo(Period.of(expectedYears, expectedMonths, expectedDays)));
    }

}