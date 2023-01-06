package org.monarchinitiative.hpo_case_annotator.model.v2;

public record AgeRangeDefault(Age onset, Age resolution) implements AgeRange {

    @Override
    public Age getStart() {
        return onset;
    }

    @Override
    public Age getEnd() {
        return resolution;
    }

}
