package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

record AgeRangeSingular(Period startAge) implements AgeRange {

    @Override
    public Period endAge() {
        return startAge;
    }

}
