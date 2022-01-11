package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

public record AgeRangeDefault(Period onset, Period resolution) implements AgeRange {
}
