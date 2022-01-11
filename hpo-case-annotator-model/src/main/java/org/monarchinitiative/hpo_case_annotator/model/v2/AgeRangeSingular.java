package org.monarchinitiative.hpo_case_annotator.model.v2;

import java.time.Period;

record AgeRangeSingular(Period onset) implements AgeRange {

    @Override
    public Period resolution() {
        return onset;
    }

}
