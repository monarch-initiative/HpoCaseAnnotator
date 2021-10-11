package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

public record AgeRangeDefault(Period startAge, Period endAge) implements AgeRange {
}
